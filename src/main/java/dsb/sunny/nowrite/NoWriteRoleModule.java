package dsb.sunny.nowrite;

import dsb.sunny.DiscordBot;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NoWriteRoleModule {

    private final Map<TextChannel, Role> noWriteChannels;
    private final Guild guild;
    private final ScheduledExecutorService t;

    private static final Logger LOG = LoggerFactory.getLogger("No-Write-Role Module");

    public NoWriteRoleModule(JDA jda) {
        this.noWriteChannels = new HashMap<>();
        this.guild = DiscordBot.getDSBGuild();

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM nowritesettings;");
            while (rs.next()) {
                noWriteChannels.put(guild.getTextChannelById(rs.getLong(1)), guild.getRoleById(rs.getLong(2)));
            }

            LOG.info("Loaded NoWrite-Channels: " + noWriteChannels.size() + " channels");
        } catch (SQLException e) {
            LOG.error("Couldn't load No-Write-Channels. Loading empty HashMap instead...");
        }

        t = Executors.newScheduledThreadPool(1);
        updateRoles();
    }

    public void updateRoles() {
        int removedRoles = 0;
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("""
                    SELECT userID, hasRole FROM nowriteroles
                    WHERE date(until) <= date('now');
                    """);

            while (rs.next()) {
                long userID = rs.getLong(1);
                long hasRole = rs.getLong(2);

                guild.retrieveMember(UserSnowflake.fromId(userID)).queue(m -> {
                    if (m != null) {
                        guild.removeRoleFromMember(UserSnowflake.fromId(userID), guild.getRoleById(hasRole)).queue();
                        guild.unloadMember(m.getIdLong());
                    }
                }, error -> {
                    LOG.warn("Could not retrieve member {}", userID);
                });
            }

            stmt = conn.createStatement();
            removedRoles = stmt.executeUpdate("DELETE FROM nowriteroles WHERE date(until) <= date('now');");
        } catch (SQLException ex) {
            LOG.error("Couldn't update roles.", ex);
        }

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime nextRun = now.withHour(2).withMinute(0).withSecond(0);
        if (now.compareTo(nextRun) >= 0)
            nextRun = nextRun.plusDays(1);
        Duration d = Duration.between(now, nextRun);
        long seconds = d.getSeconds();
        LOG.info(String.format("%d Roles were removed.", removedRoles));
        LOG.info(String.format("Next update: %s (%d seconds)",
                nextRun.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)), seconds));

        t.schedule(this::updateRoles, seconds, TimeUnit.SECONDS);
    }

    public void handleEvent(Member member, TextChannel textChannel) throws Exception {
        if (noWriteChannels.containsKey(textChannel)) {
            giveRole(member, noWriteChannels.get(textChannel));
        }
    }

    private void giveRole(Member member, Role r) throws SQLException {
        if (!member.getRoles().contains(r)) {
            try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
                        INSERT INTO nowriteroles
                        VALUES (?, ?, date('now', '+2 days'));
                        """);
                ps.setLong(1, member.getIdLong());
                ps.setLong(2, r.getIdLong());
                ps.execute();
            }
            guild.addRoleToMember(member, r).queueAfter(15, TimeUnit.MINUTES, null,
                    fail -> LOG.error("Could not assign NoWriteRole to {} (Role: {})", member.getIdLong(), r.getId()));
        }
    }

    public void setChannel(TextChannel textChannel, Role role) throws Exception {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps;
            if (noWriteChannels.containsKey(textChannel)) {
                ps = conn.prepareStatement("""
                        UPDATE nowritesettings
                        SET roleID = ?
                        WHERE channelID = ?;
                        """);
                ps.setLong(1, role.getIdLong());
                ps.setLong(2, textChannel.getIdLong());
            } else {
                ps = conn.prepareStatement("""
                        INSERT INTO nowritesettings
                        VALUES (?, ?);
                        """);
                ps.setLong(1, textChannel.getIdLong());
                ps.setLong(2, role.getIdLong());
            }
            ps.execute();
            noWriteChannels.put(textChannel, role);
        }
    }

    public void deleteChannel(TextChannel textChannel) throws Exception {
        Collection<UserSnowflake> userSnowflakes = new LinkedList<>();
        Role role = noWriteChannels.get(textChannel);
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT userID FROM nowriteroles
                    WHERE hasRole = ?;
                    """);
            ps.setLong(1, role.getIdLong());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                long id = rs.getLong(1);
                userSnowflakes.add(UserSnowflake.fromId(id));
            }

            guild.retrieveMembers(userSnowflakes).onSuccess(success -> {
                for (Member m : success) {
                    m.getGuild().removeRoleFromMember(m, role).queue();
                }
            }).onError(error -> {
                LOG.error("Could not retrieve members", error);
            });

            ps = conn.prepareStatement("DELETE FROM nowritesettings WHERE channelID = ?;");
            ps.setLong(1, textChannel.getIdLong());
            ps.execute();

            ps = conn.prepareStatement("DELETE FROM nowriteroles WHERE hasRole = ?;");
            ps.setLong(1, role.getIdLong());
            ps.execute();
            noWriteChannels.remove(textChannel);
        }
    }

    public void clearChannels() throws Exception {
        Set<TextChannel> set = noWriteChannels.keySet();
        for (TextChannel textChannel : set) {
            Collection<UserSnowflake> userSnowflakes = new LinkedList<>();
            Role role = noWriteChannels.get(textChannel);
            try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
                        SELECT userID FROM nowriteroles
                        WHERE hasRole = ?;
                        """);
                ps.setLong(1, role.getIdLong());
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    long id = rs.getLong(1);
                    userSnowflakes.add(UserSnowflake.fromId(id));
                }

                guild.retrieveMembers(userSnowflakes).onSuccess(success -> {
                    for (Member m : success) {
                        m.getGuild().removeRoleFromMember(m, role).queue();
                    }
                }).onError(error -> {
                    LOG.error("Could not retrieve members", error);
                });

                ps = conn.prepareStatement("DELETE FROM nowritesettings WHERE channelID = ?;");
                ps.setLong(1, textChannel.getIdLong());
                ps.execute();

                ps = conn.prepareStatement("DELETE FROM nowriteroles WHERE hasRole = ?;");
                ps.setLong(1, role.getIdLong());
                ps.execute();
            }
            noWriteChannels.clear();
        }
    }
}
