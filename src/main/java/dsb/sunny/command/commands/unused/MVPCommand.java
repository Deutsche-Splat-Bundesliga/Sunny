package dsb.sunny.command.commands.unused;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.mvpreport.MVPDivisionLeaderboard;
import dsb.sunny.mvpreport.MVPDivisionReportObject;
import dsb.sunny.mvpreport.MVPTeamLeaderboard;
import dsb.sunny.mvpreport.MVPTeamReportObject;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.awt.*;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;

public class MVPCommand implements SlashCommand {


    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        String sub = event.getSubcommandName();
        switch (sub) {
            case "team" -> {
                String team = event.getOption("team_name").getAsString();
                MVPTeamLeaderboard leaderboard = getMvpsOfTeam(team);
                List<String> list = getMvpOfTeamWeekly(team);

                if (leaderboard != null && list != null) {
                    StringBuilder weekList = new StringBuilder();
                    for (String s : list) {
                        weekList.append(s);
                    }

                    MessageEmbed me = new EmbedBuilder()
                            .setColor(new Color(0xC5003D))
                            .setAuthor("MVP-Liste nach Team: " + team)
                            .setDescription("Hinweis: Die MVPs werden nicht übertragen, sollte eine Spieler\\*in das Team wechseln.")
                            .addField("__**nach Wochen: **__", weekList.toString(), false)
                            .addField("__**Rangliste:**__", String.format("```%s```", leaderboard), false)
                            .setFooter("Je nach Formation kann die Rangliste sehr komisch aussehen. #DankeDiscordMobile", null)
                            .setTimestamp(OffsetDateTime.now())
                            .build();
                    event.replyEmbeds(me).setEphemeral(true).queue();
                } else {
                    event.reply("Hmmm... ich kann das Team nicht finden. Entweder gibt es den nicht oder es gab keine Eintragungen für das Team.").setEphemeral(true).queue();
                }
            }
            case "division" -> {
                String div = event.getOption("div_ordinal").getAsString().replace("Division ", "");
                MVPDivisionLeaderboard leaderboard = getDivisionLeaderboard(div);

                if (leaderboard != null) {
                    MessageEmbed me = new EmbedBuilder()
                            .setColor(SunnySettings.GENERAL.color("color"))
                            .setAuthor("MVP-Liste nach Division " + div)
                            .setDescription("Hinweis: Die MVPs werden nicht übertragen, sollte eine Spieler\\*in die Division wechseln.\n\n")
                            .appendDescription(String.format("__**Rangliste:**__%n```%s```", leaderboard))
                            .setFooter("Je nach Formation kann die Rangliste sehr komisch aussehen. #DankeDiscordMobile", null)
                            .setTimestamp(OffsetDateTime.now())
                            .build();
                    event.replyEmbeds(me).setEphemeral(true).queue();
                } else {
                    event.reply("Hmmm... ich kann die Division nicht finden. Entweder gibt es diese Division nicht, oder es gibt keine Eintragungen für diese Division.").setEphemeral(true).queue();
                }
            }
            default -> event.reply("?").setEphemeral(true).queue();
        }
    }

    private MVPDivisionLeaderboard getDivisionLeaderboard(String div) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT Team, Player, count(*) AS mvpcount
                    FROM mvp
                    WHERE Div = ?
                    GROUP BY Player
                    ORDER BY mvpcount DESC;
                    """);
            ps.setString(1, "Division " + div);
            ResultSet rs = ps.executeQuery();

            List<MVPDivisionReportObject> list = new LinkedList<>();
            int currentRank = 1;
            while (rs.next()) {
                String teamName = rs.getString(1);
                String playerName = rs.getString(2);
                int count = rs.getInt(3);

                if (list.isEmpty()) {
                    list.add(new MVPDivisionReportObject(currentRank, teamName, playerName, count));
                } else if (list.get(list.size() - 1).getCount() == count) {
                    list.get(list.size() - 1).setTied();
                    list.add(new MVPDivisionReportObject(currentRank, teamName, playerName, count));
                    list.get(list.size() - 1).setTied();
                } else {
                    currentRank = rs.getRow();
                    list.add(new MVPDivisionReportObject(currentRank, teamName, playerName, count));
                }
            }
            return list.isEmpty() ? null : new MVPDivisionLeaderboard(list);
        }
    }

    private MVPTeamLeaderboard getMvpsOfTeam(String team) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT Player, count(*) AS mvpcount
                    FROM mvp
                    WHERE Team = ?
                    GROUP BY Player
                    ORDER BY mvpcount DESC;
                    """);
            ps.setString(1, team);
            ResultSet rs = ps.executeQuery();

            List<MVPTeamReportObject> list = new LinkedList<>();
            int currentRank = 1;
            while (rs.next()) {
                String playerName = rs.getString(1);
                int count = rs.getInt(2);

                if (list.isEmpty()) {
                    list.add(new MVPTeamReportObject(currentRank, playerName, count));
                } else if (list.get(list.size() -1).getCount() == count) {
                    list.get(list.size() - 1).setTied();
                    list.add(new MVPTeamReportObject(currentRank, playerName, count));
                    list.get(list.size() - 1).setTied();
                } else {
                    currentRank = rs.getRow();
                    list.add(new MVPTeamReportObject(currentRank, playerName, count));
                }
            }
            return list.isEmpty() ? null : new MVPTeamLeaderboard(list);
        }
    }

    private List<String> getMvpOfTeamWeekly(String team) throws SQLException {
        List<String> l = new LinkedList<>();
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT RoundName, Player
                    FROM mvp
                    WHERE Team = ?
                    ORDER BY RoundName;
                    """);
            ps.setString(1, team);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                l.add(String.format("%s: **%s**%n", rs.getString(1), rs.getString(2)));
            }
        }
        return l.isEmpty() ? null : l;
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("mvp", "Gebe eine MVP-Liste nach Team oder Division aus.")
                .addSubcommands(new SubcommandData("team", "MVP-Liste nach Team (Woche für Woche)")
                        .addOption(OptionType.STRING, "team_name", "Der Name des Teams.", true))
                .addSubcommands(new SubcommandData("division", "MVP-Liste nach Division (Leaderboard)")
                        .addOption(OptionType.STRING, "div_ordinal", "Die Ordnungszahl der Division", true));
    }
}
