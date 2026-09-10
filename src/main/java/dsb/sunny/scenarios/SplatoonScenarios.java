package dsb.sunny.scenarios;

import dsb.sunny.DiscordBot;
import dsb.sunny.scenarios.scenario.Scenario;
import dsb.sunny.scenarios.scenario.ScenarioImpl;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

public class SplatoonScenarios {
    public static final int RANDOM_AMOUNT = -1;

    private List<Scenario> scenarios;
    private Category channelCategory;
    private static final Random RNG = new Random();
    private static final Logger LOG = LoggerFactory.getLogger(SplatoonScenarios.class);

    public SplatoonScenarios(Guild guild) {
        this.scenarios = new ArrayList<>();
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            conn.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS SplatoonScenarios (
                        ID INTEGER NOT NULL UNIQUE,
                        Title TEXT NOT NULL,
                        RulesDe TEXT NOT NULL,
                        RulesEn TEXT NOT NULL,
                    
                        PRIMARY KEY (ID AUTOINCREMENT)
                    );""");

            conn.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS ContradictingScenarios (
                        FirstID INTEGER NOT NULL,
                        SecondID INTEGER NOT NULL,
                    
                        PRIMARY KEY (FirstID, SecondID),
                        FOREIGN KEY (FirstID) REFERENCES SplatoonScenarios(ID) ON DELETE CASCADE,
                        FOREIGN KEY (SecondID) REFERENCES SplatoonScenarios(ID) ON DELETE CASCADE
                    );""");
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

        try {
            rebuildScenarios();
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        }

        String categoryId = SunnySettings.SCENARIOS.string("category");
        if (categoryId != null) {
            channelCategory = guild.getCategoryById(categoryId);
        }
    }

    private void rebuildScenarios() throws SQLException {
        scenarios = new ArrayList<>();
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM SplatoonScenarios");
            while (rs.next()) {
                scenarios.add(new ScenarioImpl(
                        rs.getInt("ID"),
                        rs.getString("Title"),
                        rs.getString("RulesDe"),
                        rs.getString("RulesEn")
                ));
            }

            rs = conn.createStatement().executeQuery("SELECT * FROM ContradictingScenarios");
            while (rs.next()) {
                Scenario s1 = getScenario(rs.getInt("FirstID"));
                Scenario s2 = getScenario(rs.getInt("SecondID"));

                if (s1 != null && s2 != null) {
                    s1.addContradictingScenario(s2);
                    s2.addContradictingScenario(s1);
                }
            }
        }
    }

    public void setChannelCategory(Category channelCategory) throws SQLException {
        SunnySettings.SCENARIOS.setValue("category", channelCategory.getIdLong());
        this.channelCategory = channelCategory;
    }

    public void addScenario(String title, String rulesDe, String rulesEn) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT INTO SplatoonScenarios (Title, RulesDe, RulesEn) VALUES (?, ?, ?)");
            ps.setString(1, title);
            ps.setString(2, rulesDe);
            ps.setString(3, rulesEn);
            ps.executeUpdate();
        }
        rebuildScenarios();
    }

    public void removeScenario(int id) throws SQLException, NoSuchElementException {
        if (getScenario(id) == null) {
            throw new NoSuchElementException("No such scenario with id " + id);
        }

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM SplatoonScenarios WHERE ID = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        rebuildScenarios();
    }

    public void addContradictingScenario(Scenario scenario1, Scenario scenario2) throws SQLException, IllegalArgumentException {
        if (scenario1 == null || scenario2 == null) {
            throw new IllegalArgumentException("Scenarios must not be null");
        }
        if (scenario1.equals(scenario2)) {
            throw new IllegalArgumentException("Contradicting scenarios cannot be the same");
        }

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("INSERT OR IGNORE INTO ContradictingScenarios (FirstID, SecondID) VALUES (?, ?);");
            ps.setInt(1, scenario1.getId());
            ps.setInt(2, scenario2.getId());
            ps.executeUpdate();
        }

        scenario1.addContradictingScenario(scenario2);
        scenario2.addContradictingScenario(scenario1);
    }

    public void removeContradictingScenario(Scenario scenario1, Scenario scenario2) throws SQLException, IllegalArgumentException {
        if (scenario1 == null || scenario2 == null) {
            throw new IllegalArgumentException("Scenarios must not be null");
        }
        if (scenario1.equals(scenario2)) {
            throw new IllegalArgumentException("Contradicting scenarios cannot be the same");
        }

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM ContradictingScenarios WHERE FirstID = ? AND SecondID = ?;");
            ps.setInt(1, scenario1.getId());
            ps.setInt(2, scenario2.getId());
            ps.executeUpdate();
        }

        scenario1.removeContradictingScenario(scenario2);
        scenario2.removeContradictingScenario(scenario1);
    }

    public void createChannel(Member member, String teamName) throws IllegalStateException, NoSuchElementException {
        if (channelCategory == null) {
            throw new IllegalStateException("channelCategory has not been set");
        }

        channelCategory.createTextChannel("team-" + teamName)
                // .addMemberPermissionOverride(member.getIdLong(), List.of(Permission.VIEW_CHANNEL, Permission.MESSAGE_HISTORY, Permission.USE_APPLICATION_COMMANDS, Permission.MESSAGE_SEND), null)
                .queue(channel -> {
                    EmbedBuilder eb = new EmbedBuilder()
                            .setAuthor("Splatoon-Szenarien")
                            .setDescription("""
                                    In diesem Channel findet ihr die Szenarien, die euch vom Bot präsentiert werden.
                                    Der Command für die Szenarien ist `/scenario roll`.""")
                            .addField("Ohne Befehlsparameter", "Der Bot wählt 1-2 Szenarien aus.", false)
                            .addField("Mit Befehlsparameter", "Der Bot wählt die exakte Menge an Szenarien aus.", false)
                            .setColor(new Color(0xEEFF00));

                    channel.sendMessageEmbeds(eb.build())
                            .setContent(member.getAsMention())
                            .queue();
                });
    }

    public List<Scenario> getScenarios() {
        return List.copyOf(scenarios);
    }

    public Scenario getScenario(int id) {
        return scenarios.stream().filter(scenario -> scenario.getId() == id).findFirst().orElse(null);
    }

    public List<Scenario> generateRandomScenarios(int amount) throws IllegalStateException, IllegalArgumentException {
        if (this.scenarios.isEmpty()) {
            throw new IllegalStateException("no scenarios have been added");
        }

        if (amount == RANDOM_AMOUNT) {
            amount = RNG.nextInt(1) + 1;
        }

        if (amount < 1) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }

        List<Scenario> copy = new ArrayList<>(this.scenarios);
        List<Scenario> rolled = new ArrayList<>(amount);

        while (amount-- > 0 && !copy.isEmpty()) {
            Scenario s = copy.remove(RNG.nextInt(copy.size()));
            rolled.add(s);
            copy.removeAll(s.getContradictingScenarios());
        }
        return rolled;
    }

    public void editScenario(int id, String title, String rulesDe, String rulesEn) throws SQLException, NoSuchElementException {
        Scenario s = getScenario(id);
        if (s == null) {
            throw new NoSuchElementException("scenario with id " + id + " does not exist");
        }

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE SplatoonScenarios SET Title = ?, RulesDe = ?, RulesEn = ? WHERE ID = ?;");
            ps.setString(1, title);
            ps.setString(2, rulesDe);
            ps.setString(3, rulesEn);
            ps.setInt(4, id);
            ps.executeUpdate();
        }

        rebuildScenarios();
    }
}
