package dsb.sunny.command.commands.unused;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.sql.*;
import java.time.OffsetDateTime;

public class AdminMVPCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            String sub = event.getSubcommandName();
            switch (sub) {
                case "set" -> {
                    String teamName = event.getOption("team_name").getAsString();
                    String week = event.getOption("week").getAsString();
                    String playerName = event.getOption("name").getAsString();
                    String oldest;
                    try {
                        oldest = setMVP(teamName, week, playerName);
                        oldest = oldest == null ? "*k.E.*" : oldest;
                        MessageEmbed me = new EmbedBuilder()
                                .setColor(new Color(0x00FF33))
                                .setAuthor("Die Änderung wurde erfolgreich übernommen!")
                                .setDescription("Vorgenommene Änderung für Team: " + teamName + " - Woche: " + week)
                                .addField("**__Vorgenommene Änderung:__**", String.format("```%-10s → %s```", oldest, playerName), false)
                                .setTimestamp(OffsetDateTime.now())
                                .build();

                        event.replyEmbeds(me).setEphemeral(true).queue();
                    } catch (IllegalArgumentException ex) {
                        event.reply("Hmm... Das Team gibt es in keiner Division... Hast du den Namen richtig geschrieben?").setEphemeral(true).queue();
                    }
                }
                case "del" -> {
                    String teamName = event.getOption("team_name").getAsString();
                    String week = event.getOption("week").getAsString();
                    String oldest;
                    try {
                        oldest = deleteMVP(teamName, week);
                        MessageEmbed me = new EmbedBuilder()
                                .setColor(new Color(0x00FF33))
                                .setAuthor("Die Änderung wurde erfolgreich übernommen!")
                                .setDescription("Vorgenommene Änderung für Team: " + teamName + " - Woche: " + week)
                                .addField("**__Vorgenommene Änderung:__**", String.format("```%-10s → %s", oldest, "*k.E.*"), false)
                                .setTimestamp(OffsetDateTime.now())
                                .build();
                        event.replyEmbeds(me).setEphemeral(true).queue();
                    } catch (IllegalStateException ex) {
                        event.reply("Hmm... Es gibt keine Eintragung für das Team in der Woche...").setEphemeral(true).queue();
                    }
                }
                case "clear" -> {
                    try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                        Statement stmt = conn.createStatement();
                        stmt.execute("DELETE FROM mvp;");
                    }
                    event.reply("Die MVP-Liste wurde erfolgreich gecleart!").setEphemeral(true).queue();
                }
                default -> event.reply("?").setEphemeral(true).queue();
            }
        } else {
            event.reply("Du bist kein Turnierleiter und kannst daher den Befehl nicht nutzen.").setEphemeral(true).queue();
        }
    }

    private String setMVP(String teamName, String week, String playerName) throws SQLException, IllegalArgumentException {
        String oldest = null;
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT Player FROM mvp
                    WHERE Team = ?
                    AND Week = ?;
                    """);
            ps.setString(1, teamName);
            ps.setString(2, week);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                oldest = rs.getString(1);
            }

            ps = conn.prepareStatement("SELECT Div FROM mvp WHERE Team = ? LIMIT 1;");
            ps.setString(1, teamName);
            rs = ps.executeQuery();
            if (!rs.next()) throw new IllegalArgumentException("Teamname doesn't exist in mvp!");
            String divName = rs.getString(1);

            ps = conn.prepareStatement("""
                    INSERT OR REPLACE INTO mvp
                    VALUES (?, ?, ?, ?);""");
            ps.setString(1, divName);
            ps.setString(2, teamName);
            ps.setString(3, week);
            ps.setString(4, playerName);
            ps.execute();
        }
        return oldest;
    }

    private String deleteMVP(String teamName, String week) throws SQLException, IllegalStateException {
        String oldest;
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT Player
                    FROM mvp
                    WHERE Team = ?
                    AND Week = ?;
                    """);
            ps.setString(1, teamName);
            ps.setString(2, week);
            ResultSet rs = ps.executeQuery();

            if (rs.isClosed()) throw new IllegalStateException("There is no entry!");

            oldest = rs.getString(1);
            rs.close();
            ps = conn.prepareStatement("""
                    DELETE FROM mvp
                    WHERE Team = ?
                    AND week = ?""");
            ps.setString(1, teamName);
            ps.setString(2, week);
            ps.execute();

            return oldest;
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("adminmvp", "Setze, überschreibe oder lösche MVP-Eintragungen.")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(new SubcommandData("set", "Setze oder überschreibe MVP-Eintragungen.")
                        .addOption(OptionType.STRING, "team_name", "Das betroffene Team.", true)
                        .addOption(OptionType.STRING, "week", "Die Woche, die gesetzt oder überschrieben werden soll (z.B. \"Woche 2\" oder \"Halbfinale\").", true)
                        .addOption(OptionType.STRING, "name", "Der Name des Spielers, der eingetragen werden soll.", true))
                .addSubcommands(new SubcommandData("del", "Lösche MVP-Eintragungen.")
                        .addOption(OptionType.STRING, "team_name", "Das betroffene Team.", true)
                        .addOption(OptionType.STRING, "week", "Die Woche, dessen Eintragung für das Team gelöscht werden soll.", true))
                .addSubcommands(new SubcommandData("clear", "Löscht alle MVP-Eintragungen. OBACHT! IST NICHT WIEDERHERSTELLBAR!"));

    }
}
