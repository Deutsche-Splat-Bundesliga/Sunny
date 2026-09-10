package dsb.sunny.challonge;

import at.stefangeyer.challonge.Challonge;
import at.stefangeyer.challonge.exception.DataAccessException;
import at.stefangeyer.challonge.model.Credentials;
import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.Tournament;
import at.stefangeyer.challonge.model.enumeration.MatchState;
import at.stefangeyer.challonge.model.enumeration.TournamentType;
import at.stefangeyer.challonge.model.query.MatchQuery;
import at.stefangeyer.challonge.model.query.ParticipantQuery;
import at.stefangeyer.challonge.rest.RestClient;
import at.stefangeyer.challonge.rest.retrofit.RetrofitRestClient;
import at.stefangeyer.challonge.serializer.Serializer;
import at.stefangeyer.challonge.serializer.gson.GsonSerializer;
import dsb.sunny.DiscordBot;
import dsb.sunny.challonge.report.MatchReport;
import dsb.sunny.challonge.report.MatchReportStatus;
import dsb.sunny.embeds.Embeds;
import dsb.sunny.enums.ChannelReferences;
import dsb.sunny.enums.Emotes;
import dsb.sunny.settings.SunnySettings;
import dsb.sunny.utils.SunnyUtils;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChallongeModule {

    private final Challonge challongeClient;
    private final Map<Role, Division> divisions;
    private final ScheduledExecutorService backoffStrategyTask;
    private final JDA jda;
    private static final String DSB_IDENTIFIER = "e9653c7812deb879c8dc3852";
    private static final Logger LOG = LoggerFactory.getLogger("Score Reporting Module");

    public ChallongeModule(JDA jda) {
        this.jda = jda;

        Credentials credentials = new Credentials("Sunny_DSB", DiscordBot.getProperties().getProperty("key_challonge"));
        Serializer serializer = new GsonSerializer();
        RestClient restClient = new RetrofitRestClient();
        this.challongeClient = new Challonge(credentials, serializer, restClient);
        this.backoffStrategyTask = Executors.newScheduledThreadPool(1);

        this.divisions = new HashMap<>();
        loadDivisions();
    }

    public void loadDivisions() {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM divisions");
            while (rs.next()) {
                Role r = jda.getRoleById(rs.getLong("roleID"));
                Division d = new Division(r, rs.getString("challongeID"));
                divisions.put(r, d);
            }
        } catch (SQLException ex) {
            LOG.error("Couldn't load divisions: ", ex);
        }
    }

    public void addDivision(Role r, String challongeID) throws SQLException {
        challongeID = DSB_IDENTIFIER + "-" + challongeID;
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    INSERT OR REPLACE INTO divisions
                    VALUES (?, ?);
                    """);
            ps.setLong(1, r.getIdLong());
            ps.setString(2, challongeID);
            ps.executeUpdate();

            divisions.put(r, new Division(r, challongeID));
        }
    }

    public void removeDivision(Role r) throws SQLException {
        long l = r.getIdLong();
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM divisions WHERE roleID = ?;");
            ps.setLong(1, l);
            ps.execute();
        }
        divisions.remove(r);
    }

    public void clearDivisions() throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM divisions;");
        }
        divisions.clear();
    }

    public void reportScore(@NotNull ModalInteractionEvent event) {
        reportScore(event, 1, 1);
    }

    private void reportScore(@NotNull ModalInteractionEvent event, int attempt, int nextAttemptIn) {
        Role divRole = null;
        String reportingTeam;
        String opponentTeam = event.getValue("opponent").getAsString().trim();
        String score = event.getValue("score").getAsString().trim();
        // String mvpReporting = event.getValue("teammvp1").getAsString().trim();
        // String mvpOpponent = event.getValue("teammvp2").getAsString().trim();
        String memberName = event.getMember().getNickname();

        for (Role r : event.getMember().getRoles()) {
            if (r.getName().contains("Division")) {
                divRole = r;
                break;
            }
        }

        if (memberName != null && divRole != null) {
            Regex reportingTeamRegex = new Regex("\\[(?<Teamname>.+)\\]");
            reportingTeam = reportingTeamRegex.find(memberName, 0).getGroupValues().get(1).trim();
            try {
                MatchReport report = createMatchReport(divRole, reportingTeam, opponentTeam, score, false);
                switch (report.getStatus()) {
                    case OK -> {
                        EmbedBuilder eb = Embeds.byRole(report.getDivision().divisionRole())
                                .setTitle(String.format("%s • %s", report.getDivision().divisionRole().getName(),
                                        report.getWeekName()), report.getDivisionUrl())
                                .setDescription(String.format("%s **%s-%s** %s", report.getTeam1(), report.getScore1(),
                                        report.getScore2(), report.getTeam2()))
                                .setFooter(String.format("Reported by: %s", event.getMember().getNickname()));

                        /*
                         * if (!report.isForfeited()) {
                         * eb.addField(new MessageEmbed.Field("MVP " + report.getTeam1(), mvpReporting,
                         * true))
                         * .addField(new MessageEmbed.Field("MVP " + report.getTeam2(), mvpOpponent,
                         * true));
                         * } else {
                         * eb.addField(new MessageEmbed.Field("Wichtiger Hinweis: ",
                         * "Keine MVPs: Spiel fand nicht statt.", false));
                         * }
                         */

                        if (report.isForfeited()) {
                            eb.addField("Wichtiger Hinweis: ", "Keine MVPs: Spiel fand nicht statt.", false);
                        }

                        long id = SunnySettings.SCORE_REPORT.aLong("channel");
                        TextChannel reportChannel = event.getGuild().getTextChannelById(id);

                        if (reportChannel == null) {
                            event.getHook().editOriginal(
                                    "Das Ergebnis ist eingetragen, jedoch gibt es anscheinend kein Ergebnisse-Channel mehr.")
                                    .queue();
                        } else if (!reportChannel.canTalk()) {
                            event.getHook().editOriginal(
                                    "Das Ergebnis ist eingetragen, jedoch kann ich keine Ergebnisse auf dem eingestellten Channel posten.")
                                    .queue();
                        } else {
                            reportChannel.sendMessageEmbeds(eb.build()).queue();
                            event.getHook().editOriginal("Das Ergebnis wurde erfolgreich reported!").queue();
                        }
                    }
                    case TEAM1_IS_NULL ->
                        event.getHook().editOriginal(String.format(
                                "Hmm... ich kann dich auf Challonge nicht finden - dein Nickname ist entweder falsch oder zu ungenau. Melde dich beim %s",
                                ChannelReferences.HELPDESK)).queue();
                    case TEAM2_IS_NULL ->
                        event.getHook().editOriginal(
                                "Hmm... ich kann deinen Gegner auf Challonge nicht finden - der Name ist entweder falsch oder zu ungenau. Bitte versuche es nochmal.")
                                .queue();
                    case SAME_NAME ->
                        event.getHook().editOriginal(
                                "Spielst du auch selber gegen dich Schach und verlierst? I know that feeling, i know...")
                                .queue();
                    case ILLEGAL_SCORE ->
                        event.getHook().editOriginal(
                                "Der Punktestand, den du mir gegeben hast, ist fehlerhaft. Überprüfe es bitte nochmal.")
                                .queue();
                    case MATCH_404 ->
                        event.getHook().editOriginal(
                                "Ich kann das Match nicht finden... Bitte kontaktiere die TOs, wenn du glaubst, es sei ein Fehler.")
                                .queue();
                    case ILLEGAL_MVP_REPORT ->
                        event.getHook().editOriginal(
                                "Die MVPs sind falsch eingetragen - bitte überprüfe nochmal deine Eingaben. Falls ein Spiel gespielt worden ist, müssen MVPs eingetragen werden!")
                                .queue();
                    case ALREADY_REPORTED ->
                        event.getHook().editOriginal(String.format(
                                "Das Spiel ist schon bereits reported worden. Bitte kontaktiere %s, wenn du der Meinung bist, dass das falsch ist.",
                                ChannelReferences.HELPDESK)).queue();
                    case NO_DIVISION ->
                        event.getHook()
                                .editOriginal(String.format(
                                        "Die Division scheint noch nicht eingetragen zu sein. Bitte melde dich beim %s",
                                        ChannelReferences.HELPDESK))
                                .queue();
                }

                String changeLogChannelId = SunnySettings.CHANGELOG.string("channel");
                if (changeLogChannelId == null) {
                    LOG.error("changelogchannel can not be retrieved: null");
                } else {
                    TextChannel changelogChannel = event.getGuild().getTextChannelById(changeLogChannelId);
                    if (changelogChannel == null) {
                        LOG.error("Changelog channel does not exist");

                    } else if (!changelogChannel.canTalk()) {
                        LOG.error("Cannot send messages in changelog channel.");

                    } else {
                        MessageEmbed eb = Embeds.changelog(event.getMember(), "Report-Command")
                                .addField("Status Match-Report:", Embeds.code(report.getStatus().name()), false)
                                .addField("Gegner:", Embeds.code(opponentTeam), false)
                                .addField("Punktestand:", Embeds.code(score), false)
                                // .addField("MVP Reporting:", Embeds.code(mvpReporting), false)
                                // .addField("MVP Gegner:", Embeds.code(mvpOpponent), false)
                                .build();
                        changelogChannel.sendMessageEmbeds(eb).queue();
                    }
                }
            } catch (DataAccessException ex) {
                if (++attempt >= 5) {
                    LOG.error("Couldn't report score: ", ex);
                    event.getHook().editOriginal(String.format(
                            "Fehler von der Challonge-API. Bitte versuche es in ein paar Minuten erneut. Sollte es dann immer noch nicht funktionieren, melde dich beim %s und pinge <@339429839318810635>.",
                            ChannelReferences.HELPDESK)).queue();
                } else {
                    final int nextAttempt = nextAttemptIn * 2, attemptCount = attempt;
                    event.getHook().editOriginal("Fehler von der Challonge-API. Wir versuchen es nochmal...\n" +
                            String.format("Versuch: %d - <t:%d:R>", attemptCount,
                                    Instant.now().getEpochSecond() + nextAttempt))
                            .queue();

                    backoffStrategyTask.schedule(() -> reportScore(event, attemptCount, nextAttempt), nextAttempt,
                            TimeUnit.SECONDS);
                }
            } catch (SQLException ex) {
                LOG.error("Couldn't report score: ", ex);
                event.getHook()
                        .editOriginal(String.format(
                                "Fehler in der Datenbank. Bitte melde dich beim %s und pinge <@339429839318810635>.",
                                ChannelReferences.HELPDESK))
                        .queue();
            } catch (Exception ex) {
                LOG.error("Couldn't report score: ", ex);
                event.getHook()
                        .editOriginal(String.format(
                                "Unerwarteter Fehler. Bitte melde dich beim %s und pinge <@339429839318810635>.",
                                ChannelReferences.HELPDESK))
                        .queue();
            }
        }
    }

    public void editScore(@NotNull ModalInteractionEvent event, Role divRole) {
        editScore(event, divRole, 1, 1);
    }

    private void editScore(ModalInteractionEvent event, Role divRole, int attempt, int nextAttemptIn) {
        String team1 = event.getValue("team1").getAsString();
        String team2 = event.getValue("team2").getAsString();
        String score = event.getValue("score").getAsString();
        // String mvp1 = event.getValue("mvp1").getAsString();
        // String mvp2 = event.getValue("mvp2").getAsString();
        if (!score.isEmpty()) {
            try {
                MatchReport report = createMatchReport(divRole, team1, team2, score, true);

                switch (report.getStatus()) {
                    case OK -> {
                        StringBuilder sb = new StringBuilder();
                        EmbedBuilder eb = Embeds.byRole(report.getDivision().divisionRole())
                                .setTitle(
                                        String.format("%s • %s (KORREKTUR)",
                                                report.getDivision().divisionRole().getName(), report.getWeekName()),
                                        report.getDivisionUrl())
                                .setDescription(String.format("%s **%s-%s** %s", report.getTeam1(), report.getScore1(),
                                        report.getScore2(), report.getTeam2()))
                                .setFooter(String.format("Corrected by: %s", event.getUser().getName()));

                        if (report.getScore1() == -1 || report.getScore2() == -1) {
                            eb.setDescription(
                                    String.format("%s *(kein Score)* %s", report.getTeam1(), report.getTeam2()));
                        }
                        sb.append("Die Punktzahl wurde korrigiert.\n");
                        /*
                         * if (!mvp1.isEmpty()) {
                         * sb.append(String.format("Der MVP von %s wurde korrigiert.%n",
                         * report.getTeam2()));
                         * }
                         * if (!mvp2.isEmpty()) {
                         * sb.append(String.format("Der MVP von %s wurde korrigiert.%n",
                         * report.getTeam2()));
                         * }
                         * 
                         * if (!report.isForfeited()) {
                         * eb.addField(String.format("MVP %s", report.getTeam1()),
                         * !report.getMVP1().isEmpty() ? report.getMVP1() : "*kein MVP*", true);
                         * eb.addField(String.format("MVP %s", report.getTeam2()),
                         * !report.getMVP2().isEmpty() ? report.getMVP2() : "*kein MVP*", true);
                         * } else {
                         * eb.addField("Wichtiger Hinweis: ", "Keine MVPs: Spiel fand nicht statt.",
                         * false);
                         * }
                         */

                        if (report.isForfeited()) {
                            eb.addField("Wichtiger Hinweis: ", "Keine MVPs: Spiel fand nicht statt.", false);
                        }

                        if (!sb.isEmpty()) {
                            eb.addField("Korrekturen:", sb.toString(), false);
                        }

                        long id = SunnySettings.SCORE_REPORT.aLong("channel");
                        TextChannel reportChannel = event.getGuild().getTextChannelById(id);

                        if (reportChannel == null) {
                            event.getHook().editOriginal(
                                    "Das Ergebnis ist korrigiert, jedoch gibt es anscheinend kein Ergebnisse-Channel mehr.")
                                    .queue();
                        } else if (!reportChannel.canTalk()) {
                            event.getHook().editOriginal(
                                    "Das Ergebnis ist korrigiert, jedoch kann ich keine Ergebnisse auf den eingestellten Channel posten.")
                                    .queue();
                        } else {
                            reportChannel.sendMessageEmbeds(eb.build()).queue();
                            event.getHook().editOriginal("Das Ergebnis wurde erfolgreich korrigiert!").queue();
                        }
                    }
                    case TEAM1_IS_NULL -> event.getHook()
                            .editOriginal(
                                    "Das erste Team konnte nicht gefunden werden oder die Eingabe ist zu ungenau.")
                            .queue();
                    case TEAM2_IS_NULL -> event.getHook()
                            .editOriginal(
                                    "Das zweite Team konnte nicht gefunden werden oder die Eingabe ist zu ungenau.")
                            .queue();
                    case SAME_NAME -> event.getHook().editOriginal("Beide Teams sind identisch.").queue();
                    case ILLEGAL_SCORE ->
                        event.getHook().editOriginal("Die Punktzahl, die du mir gegeben hast, ist falsch.").queue();
                    case MATCH_404 -> event.getHook()
                            .editOriginal("Es konnte kein Match mit den beiden Teams gefunden werden.").queue();
                    case NO_DIVISION ->
                        event.getHook().editOriginal("Die Division ist nicht in der Datenbank.").queue();
                    default -> event.getHook()
                            .editOriginal("Durfte eigentlich nicht passieren, aber hey. " + report.getStatus()).queue();
                }

                String changeLogChannelId = SunnySettings.CHANGELOG.string("channel");
                if (changeLogChannelId == null) {
                    LOG.error("changelogchannel can not be retrieved: null");
                } else {
                    TextChannel changelogChannel = event.getGuild().getTextChannelById(changeLogChannelId);
                    if (changelogChannel == null) {
                        LOG.error("Changelog channel does not exist");

                    } else if (!changelogChannel.canTalk()) {
                        LOG.error("Cannot send messages in changelog channel.");

                    } else {
                        EmbedBuilder eb = Embeds.changelog(event.getMember(), "Edit-Score-Command")
                                .addField("Status Match-Report:", Embeds.code(report.getStatus().name()), false)
                                .addField("Erstes Team:", Embeds.code(team1), false)
                                .addField("Zweites Team:", Embeds.code(team2), false);
                        if (!score.isEmpty()) {
                            eb.addField("Punktestand:", Embeds.code(score), false);
                        }
                        /*
                         * if (!mvp1.isEmpty()) {
                         * eb.addField("MVP Reporting:", String.format("`%s`", mvp1), false);
                         * }
                         * if (!mvp2.isEmpty()) {
                         * eb.addField("MVP Gegner:", String.format("`%s`", mvp2), false);
                         * }
                         */
                        changelogChannel.sendMessageEmbeds(eb.build()).queue();
                    }
                }
            } catch (DataAccessException ex) {
                if (++attempt >= 5) {
                    LOG.error("Couldn't report score: ", ex);
                    event.getHook().editOriginal(String.format(
                            "Fehler von der Challonge-API. Bitte melde dich beim %s und pinge <@339429839318810635>.",
                            ChannelReferences.HELPDESK)).queue();
                } else {
                    final int nextAttempt = nextAttemptIn * 2, attemptCount = attempt;
                    event.getHook()
                            .editOriginal(String.format(
                                    "%s Fehler von der Challonge-API. Wir versuchen es nochmal...%n", Emotes.WAIT) +
                                    String.format("Versuch: %d - <t:%d:R>", attemptCount,
                                            Instant.now().getEpochSecond() + nextAttempt))
                            .queue();
                    LOG.error("Score-Report attempt failed: ", ex);
                    backoffStrategyTask.schedule(() -> editScore(event, divRole, attemptCount, nextAttempt),
                            nextAttempt, TimeUnit.SECONDS);
                }
            } catch (SQLException ex) {
                LOG.error("Couldn't report score: ", ex);
                event.getHook()
                        .editOriginal(String.format(
                                "Fehler in der Datenbank. Bitte melde dich beim %s und pinge <@339429839318810635>.",
                                ChannelReferences.HELPDESK))
                        .queue();
            } catch (Exception ex) {
                LOG.error("Couldn't report score: ", ex);
                event.getHook()
                        .editOriginal(String.format(
                                "Unerwarteter Fehler. Bitte melde dich beim %s und pinge <@339429839318810635>.",
                                ChannelReferences.HELPDESK))
                        .queue();
            }

        } else {
            event.getHook().editOriginal("Ich kann schlecht was korrigieren, wenn es nichts zu korrigieren gibt.")
                    .queue();
        }
    }

    private MatchReport createMatchReport(Role division, String team1, String team2, String score, boolean edit)
            throws DataAccessException, SQLException {
        Division div = divisions.get(division);

        if (div == null) {
            return new MatchReport(MatchReportStatus.NO_DIVISION);
        }

        Tournament bracket = challongeClient.getTournament(div.challongeID(), true, true);
        List<Participant> allParticipants = bracket.getParticipants();
        Participant participant1 = ChallongeUtils.getParticipant(team1, allParticipants);
        Participant participant2 = ChallongeUtils.getParticipant(team2, allParticipants);

        if (participant1 == null) {
            return new MatchReport(MatchReportStatus.TEAM1_IS_NULL);
        } else if (participant2 == null) {
            return new MatchReport(MatchReportStatus.TEAM2_IS_NULL);
        } else if (participant1.equals(participant2)) {
            return new MatchReport(MatchReportStatus.SAME_NAME);
        }

        List<Match> participant1Matches = ChallongeUtils.getMatches(participant1, bracket.getMatches());

        if (bracket.getTournamentType().equals(TournamentType.ROUND_ROBIN)) {
            return roundRobinOnly(div, bracket, participant1, participant1Matches, participant2, score, edit);
        }

        boolean inPlayoffs = ChallongeUtils.hasPlayoffMatches(bracket);

        Match match = ChallongeUtils.getActiveMatch(participant1Matches, participant2, inPlayoffs);

        if (match == null) {
            return new MatchReport(MatchReportStatus.MATCH_404);
        }

        boolean reversed = match.getPlayer2Id().equals(participant1.getId()) || match.getPlayer2Id()
                .equals(participant1.getGroupPlayerIds().isEmpty() ? null : participant1.getGroupPlayerIds().get(0));

        if (match.getState().equals(MatchState.COMPLETE) && !edit) {
            return new MatchReport(MatchReportStatus.ALREADY_REPORTED);
        }

        boolean forfeited = false;
        int score1 = 0, score2 = 0;

        if (!score.isEmpty()) {
            MatchResult scoresMatchResult = ChallongeUtils.SCORE_REGEX.find(score, 0);
            if (scoresMatchResult == null) {
                return new MatchReport(MatchReportStatus.ILLEGAL_SCORE);
            }

            String s1 = scoresMatchResult.getGroupValues().get(1);
            String s2 = scoresMatchResult.getGroupValues().get(2);

            if (s1.equalsIgnoreCase("ff")) {
                forfeited = true;
            } else {
                score1 = Integer.parseInt(s1);
            }

            if (s2.equalsIgnoreCase("ff")) {
                forfeited = true;
                score1 = 5;
            } else {
                score2 = s1.equalsIgnoreCase("ff") ? 5 : Integer.parseInt(s2);
            }

            if ((s1.equalsIgnoreCase("ff") && s2.equalsIgnoreCase("ff")) || (score1 < 0 || score2 < 0)
                    || (score1 != 5 && score2 != 5)) {
                return new MatchReport(MatchReportStatus.ILLEGAL_SCORE);
            }

            /*
             * if (!edit && (!forfeited && (mvpTeam1.isEmpty() || mvpTeam2.isEmpty()))) {
             * return new MatchReport(MatchReportStatus.ILLEGAL_MVP_REPORT);
             * }
             */

            MatchQuery.MatchQueryBuilder matchQuery = MatchQuery.builder()
                    .scoresCsv(reversed ? score2 + "-" + score1 : score1 + "-" + score2);
            if (inPlayoffs) {
                matchQuery.winnerId(
                        score1 > score2 ? participant1.getId() : score2 > score1 ? participant2.getId() : null);
            } else {
                matchQuery.winnerId(score1 > score2 ? participant1.getGroupPlayerIds().get(0)
                        : score2 > score1 ? participant2.getGroupPlayerIds().get(0) : null);
            }

            challongeClient.updateMatch(match, matchQuery.build());
        } else if (!edit) {
            return new MatchReport(MatchReportStatus.ILLEGAL_SCORE);
        } else if (match.getState().equals(MatchState.OPEN)) {
            score1 = -1;
            score2 = -1;
        } else {
            Long groupId = participant1.getGroupPlayerIds().isEmpty() ? null : participant1.getGroupPlayerIds().get(0);
            score = match.getPlayer1Id().equals(participant1.getId()) || match.getPlayer1Id().equals(groupId)
                    ? match.getScoresCsv()
                    : new StringBuilder(match.getScoresCsv()).reverse().toString();
            score1 = Integer.parseInt(ChallongeUtils.SCORE_REGEX.find(score, 0).getGroupValues().get(1));
            score2 = Integer.parseInt(ChallongeUtils.SCORE_REGEX.find(score, 0).getGroupValues().get(2));
        }

        /*
         * if (bracket.getAcceptAttachments()) {
         * try {
         * challongeClient.createAttachment(match,
         * AttachmentQuery.builder().description(String.format("MVP %s: %s | MVP %s: %s"
         * , participant1.getName(), mvpTeam1, participant2.getName(), mvpTeam2))
         * .build());
         * } catch (DataAccessException ex) {
         * LOG.error("Couldn't add attachment to match {}", match);
         * }
         * }
         */

        String roundName;
        if (inPlayoffs) {
            roundName = switch (match.getRound()) {
                case 1 -> "Halbfinale";
                case 2 -> "Finale";
                default -> "Spiel um Platz 3";
            };
        } else {
            roundName = String.format("Woche %d", match.getRound());
        }

        /*
         * try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
         * PreparedStatement ps;
         * if (!forfeited) {
         * ps = conn.prepareStatement("""
         * INSERT OR REPLACE INTO mvp
         * VALUES (?, ?, ?, ?);
         * """);
         * 
         * if (!mvpTeam1.isEmpty()) {
         * ps.setString(1, division.getName());
         * ps.setString(2, participant1.getName());
         * ps.setString(3, roundName);
         * ps.setString(4, mvpTeam1);
         * ps.addBatch();
         * }
         * 
         * if (!mvpTeam2.isEmpty()) {
         * ps.setString(1, division.getName());
         * ps.setString(2, participant2.getName());
         * ps.setString(3, roundName);
         * ps.setString(4, mvpTeam2);
         * ps.addBatch();
         * }
         * 
         * if (!mvpTeam1.isEmpty() || !mvpTeam2.isEmpty()) ps.executeBatch();
         * } else if (edit) {
         * ps = conn.prepareStatement("""
         * DELETE FROM mvp
         * WHERE Div = ? AND Team = ? AND RoundName = ?;
         * """);
         * 
         * ps.setString(1, division.getName());
         * ps.setString(2, participant1.getName());
         * ps.setString(3, roundName);
         * ps.addBatch();
         * 
         * ps.setString(1, division.getName());
         * ps.setString(2, participant2.getName());
         * ps.setString(3, roundName);
         * ps.addBatch();
         * 
         * ps.executeBatch();
         * }
         * 
         * if (edit) {
         * ps = conn.prepareStatement("""
         * SELECT Team, Player FROM mvp
         * WHERE Div = ? AND RoundName = ? AND Team IN(?, ?);""");
         * ps.setString(1, division.getName());
         * ps.setString(2, roundName);
         * ps.setString(3, participant1.getName());
         * ps.setString(4, participant2.getName());
         * ResultSet rs = ps.executeQuery();
         * 
         * while (rs.next()) {
         * if (rs.getString(1).equalsIgnoreCase(participant1.getName())) {
         * mvpTeam1 = rs.getString(2);
         * } else {
         * mvpTeam2 = rs.getString(2);
         * }
         * }
         * }
         * }
         */

        return new MatchReport(MatchReportStatus.OK, div, participant1.getName(), participant2.getName(),
                bracket.getFullChallongeUrl(), roundName, score1, score2, forfeited, edit, inPlayoffs);
    }

    private MatchReport roundRobinOnly(Division div, Tournament bracket, Participant participant1,
            List<Match> participant1Matches, Participant participant2, String score, boolean edit)
            throws DataAccessException {
        Match match = ChallongeUtils.getActiveMatch(participant1Matches, participant2, true);

        if (match == null) {
            return new MatchReport(MatchReportStatus.MATCH_404);
        }

        boolean reversed = match.getPlayer2Id().equals(participant1.getId()) || match.getPlayer2Id()
                .equals(participant1.getGroupPlayerIds().isEmpty() ? null : participant1.getGroupPlayerIds().get(0));

        if (match.getState().equals(MatchState.COMPLETE) && !edit) {
            return new MatchReport(MatchReportStatus.ALREADY_REPORTED);
        }

        boolean forfeited = false;
        int score1 = 0, score2 = 0;

        if (!score.isEmpty()) {
            MatchResult scoresMatchResult = ChallongeUtils.SCORE_REGEX.find(score, 0);
            if (scoresMatchResult == null) {
                return new MatchReport(MatchReportStatus.ILLEGAL_SCORE);
            }

            String s1 = scoresMatchResult.getGroupValues().get(1);
            String s2 = scoresMatchResult.getGroupValues().get(2);

            if (s1.equalsIgnoreCase("ff")) {
                forfeited = true;
            } else {
                score1 = Integer.parseInt(s1);
            }

            if (s2.equalsIgnoreCase("ff")) {
                forfeited = true;
                score1 = 5;
            } else {
                score2 = s1.equalsIgnoreCase("ff") ? 5 : Integer.parseInt(s2);
            }

            if ((s1.equalsIgnoreCase("ff") && s2.equalsIgnoreCase("ff")) || (score1 < 0 || score2 < 0)
                    || (score1 != 5 && score2 != 5)) {
                return new MatchReport(MatchReportStatus.ILLEGAL_SCORE);
            }

            /*
             * if (!edit && (!forfeited && (mvpTeam1.isEmpty() || mvpTeam2.isEmpty()))) {
             * return new MatchReport(MatchReportStatus.ILLEGAL_MVP_REPORT);
             * }
             */

            MatchQuery.MatchQueryBuilder matchQuery = MatchQuery.builder()
                    .scoresCsv(reversed ? score2 + "-" + score1 : score1 + "-" + score2);
            matchQuery.winnerId(score1 > score2 ? participant1.getId() : score2 > score1 ? participant2.getId() : null);
            challongeClient.updateMatch(match, matchQuery.build());
        } else if (!edit) {
            return new MatchReport(MatchReportStatus.ILLEGAL_SCORE);
        } else if (match.getState().equals(MatchState.OPEN)) {
            score1 = -1;
            score2 = -1;
        } else {
            Long groupId = participant1.getGroupPlayerIds().isEmpty() ? null : participant1.getGroupPlayerIds().get(0);
            score = match.getPlayer1Id().equals(participant1.getId()) || match.getPlayer1Id().equals(groupId)
                    ? match.getScoresCsv()
                    : new StringBuilder(match.getScoresCsv()).reverse().toString();
            score1 = Integer.parseInt(ChallongeUtils.SCORE_REGEX.find(score, 0).getGroupValues().get(1));
            score2 = Integer.parseInt(ChallongeUtils.SCORE_REGEX.find(score, 0).getGroupValues().get(2));
        }

        /*
         * if (bracket.getAcceptAttachments()) {
         * try {
         * challongeClient.createAttachment(match,
         * AttachmentQuery.builder().description(String.format("MVP %s: %s | MVP %s: %s"
         * , participant1.getName(), mvpTeam1, participant2.getName(), mvpTeam2))
         * .build());
         * } catch (DataAccessException ex) {
         * LOG.error("Couldn't add attachment to match {}", match);
         * }
         * }
         */

        String roundName = String.format("Woche %d", match.getRound());

        /*
         * try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
         * PreparedStatement ps;
         * if (!forfeited) {
         * ps = conn.prepareStatement("""
         * INSERT OR REPLACE INTO mvp
         * VALUES (?, ?, ?, ?);
         * """);
         * 
         * if (!mvpTeam1.isEmpty()) {
         * ps.setString(1, division.getName());
         * ps.setString(2, participant1.getName());
         * ps.setString(3, roundName);
         * ps.setString(4, mvpTeam1);
         * ps.addBatch();
         * }
         * 
         * if (!mvpTeam2.isEmpty()) {
         * ps.setString(1, division.getName());
         * ps.setString(2, participant2.getName());
         * ps.setString(3, roundName);
         * ps.setString(4, mvpTeam2);
         * ps.addBatch();
         * }
         * 
         * if (!mvpTeam1.isEmpty() || !mvpTeam2.isEmpty()) ps.executeBatch();
         * } else if (edit) {
         * ps = conn.prepareStatement("""
         * DELETE FROM mvp
         * WHERE Div = ? AND Team = ? AND RoundName = ?;
         * """);
         * 
         * ps.setString(1, division.getName());
         * ps.setString(2, participant1.getName());
         * ps.setString(3, roundName);
         * ps.addBatch();
         * 
         * ps.setString(1, division.getName());
         * ps.setString(2, participant2.getName());
         * ps.setString(3, roundName);
         * ps.addBatch();
         * 
         * ps.executeBatch();
         * }
         * 
         * if (edit) {
         * ps = conn.prepareStatement("""
         * SELECT Team, Player FROM mvp
         * WHERE Div = ? AND RoundName = ? AND Team IN(?, ?);""");
         * ps.setString(1, division.getName());
         * ps.setString(2, roundName);
         * ps.setString(3, participant1.getName());
         * ps.setString(4, participant2.getName());
         * ResultSet rs = ps.executeQuery();
         * 
         * while (rs.next()) {
         * if (rs.getString(1).equalsIgnoreCase(participant1.getName())) {
         * mvpTeam1 = rs.getString(2);
         * } else {
         * mvpTeam2 = rs.getString(2);
         * }
         * }
         * }
         * }
         */

        return new MatchReport(MatchReportStatus.OK, div, participant1.getName(), participant2.getName(),
                bracket.getFullChallongeUrl(), roundName, score1, score2, forfeited, edit, false);
    }

    private void dropTeam(String teamName, Role divRole, String action) throws Exception {
        Division division = divisions.get(divRole);

        Tournament divisionChallonge = challongeClient.getTournament(division.challongeID(), true, true);
        Participant droppedTeam = null;
        for (Participant p : divisionChallonge.getParticipants()) {
            if (p.getName().equalsIgnoreCase(teamName)) {
                droppedTeam = p;
                break;
            }
        }

        if (droppedTeam != null) {
            List<Match> allMatches = divisionChallonge.getMatches();
            List<Match> droppedTeamMatches = new ArrayList<>();
            for (Match m : allMatches) {

                Long droppedId = droppedTeam.getId(), droppedGroupId = droppedTeam.getGroupPlayerIds().isEmpty() ? null
                        : droppedTeam.getGroupPlayerIds().get(0);
                if (m.getPlayer1Id().equals(droppedId) || m.getPlayer1Id().equals(droppedGroupId)
                        || m.getPlayer2Id().equals(droppedId) || m.getPlayer2Id().equals(droppedGroupId)) {
                    droppedTeamMatches.add(m);
                }
            }

            switch (action) {
                case "nullify" -> {
                    for (Match m : droppedTeamMatches) {
                        Long droppedId = droppedTeam.getId(),
                                droppedGroupId = droppedTeam.getGroupPlayerIds().isEmpty() ? null
                                        : droppedTeam.getGroupPlayerIds().get(0);
                        Long opponentId = m.getPlayer1Id().equals(droppedTeam.getId())
                                || m.getPlayer1Id().equals(droppedGroupId) ? m.getPlayer2Id() : m.getPlayer1Id();
                        boolean opponentDroppedToo = false;
                        Regex scoreRegex = new Regex("(?<Team1Score>-?\\d)-(?<Team2Score>-?\\d)");
                        MatchResult mr = scoreRegex.find(m.getScoresCsv(), 0);
                        if (mr != null) {
                            List<String> groupRegex = mr.getGroupValues();
                            for (String score : groupRegex) {
                                if (groupRegex.indexOf(score) != 0 && Integer.parseInt(score) < 0) {
                                    opponentDroppedToo = true;
                                    break;
                                }
                            }
                        }

                        boolean reversed = m.getPlayer2Id().equals(droppedId)
                                || m.getPlayer2Id().equals(droppedGroupId);

                        if (m.getState().equals(MatchState.COMPLETE))
                            challongeClient.reopenMatch(m);
                        MatchQuery.MatchQueryBuilder mq = MatchQuery.builder()
                                .scoresCsv(opponentDroppedToo ? "-1--1" : reversed ? "0--1" : "-1-0");
                        if (opponentDroppedToo) {
                            mq = mq.tie();
                        } else {
                            mq = mq.winnerId(opponentId);
                        }
                        challongeClient.updateMatch(m, mq.build());
                    }
                }
                case "score" -> {
                    for (Match m : droppedTeamMatches) {
                        if (!m.getState().equals(MatchState.COMPLETE)) {
                            Long droppedId = droppedTeam.getId(),
                                    droppedGroupId = droppedTeam.getGroupPlayerIds().isEmpty() ? null
                                            : droppedTeam.getGroupPlayerIds().get(0);
                            Long opponentId = m.getPlayer1Id().equals(droppedTeam.getId())
                                    || m.getPlayer1Id().equals(droppedGroupId) ? m.getPlayer2Id() : m.getPlayer1Id();
                            boolean reversed = m.getPlayer2Id().equals(droppedId)
                                    || m.getPlayer2Id().equals(droppedGroupId);

                            MatchQuery mq = MatchQuery.builder()
                                    .winnerId(opponentId)
                                    .scoresCsv(reversed ? "5-0" : "0-5")
                                    .build();
                            challongeClient.updateMatch(m, mq);
                        }
                    }
                }
                case "reject" -> {
                }
                default ->
                    throw new IllegalArgumentException("Unallowed action for ChallongeModule#dropTeam: " + action);
            }
            ParticipantQuery pq = ParticipantQuery.builder()
                    .name(droppedTeam.getName() + " (dropped)")
                    .build();
            if (!action.equalsIgnoreCase("reject")) {
                long id = SunnySettings.SCORE_REPORT.aLong("channel");
                TextChannel reportChannel = DiscordBot.getDSBGuild().getTextChannelById(id);

                if (reportChannel != null && reportChannel.canTalk()) {
                    String decision = action.equalsIgnoreCase("nullify")
                            ? String.format("Alle Spiele von %s wurden annulliert.", droppedTeam.getName())
                            : String.format("Alle restlichen Spiele von %s werden 0-5 für den Gegner gewertet.",
                                    droppedTeam.getName());
                    EmbedBuilder eb = Embeds.byRole(divRole)
                            .setTitle(divRole.getName(), divisionChallonge.getFullChallongeUrl())
                            .setDescription(String.format("%s**%s** hat die Liga verlassen.%n%s", Emotes.LEAVE,
                                    droppedTeam.getName(), decision));

                    reportChannel.sendMessageEmbeds(eb.build()).queue();
                }
                challongeClient.updateParticipant(droppedTeam, pq);
            }
        } else
            throw new IllegalStateException("There is no team in division named '" + teamName + "'!");
    }

    public DropRequest createDropRequest(Role divRole, String teamName, Member captain)
            throws DataAccessException, SQLException {
        if (hasActiveDropRequest(teamName))
            return null;

        int id;
        int[] percentDivisionPlayed = new int[2];
        int[] percentTeamPlayed = new int[2];

        Division div = divisions.get(divRole);

        if (div != null) {
            Tournament challongeDivision = challongeClient.getTournament(div.challongeID(), true, true);
            List<Match> allMatches = challongeDivision.getMatches();
            List<Match> droppedTeamMatches = new ArrayList<>();
            Participant dropped = null;
            percentDivisionPlayed[1] = allMatches.size();
            for (Participant p : challongeDivision.getParticipants()) {
                if (p.getName().toLowerCase(Locale.ROOT).contains(teamName.toLowerCase(Locale.ROOT))) {
                    dropped = p;
                    break;
                }
            }

            if (dropped == null) {
                return null;
            }

            for (Match m : allMatches) {
                if (m.getState().equals(MatchState.COMPLETE))
                    percentDivisionPlayed[0]++;

                Long droppedId = dropped.getId(), droppedGroupId = dropped.getGroupPlayerIds().isEmpty() ? null
                        : dropped.getGroupPlayerIds().get(0);
                if (m.getPlayer1Id().equals(droppedId) || m.getPlayer1Id().equals(droppedGroupId)
                        || m.getPlayer2Id().equals(droppedId) || m.getPlayer2Id().equals(droppedGroupId)) {
                    droppedTeamMatches.add(m);
                }
            }

            percentTeamPlayed[1] = droppedTeamMatches.size();
            for (Match m : droppedTeamMatches) {
                if (m.getState().equals(MatchState.COMPLETE))
                    percentTeamPlayed[0]++;
            }

            id = insertAndGetID(captain, divRole, teamName);
            return new DropRequest(id, captain, divRole, dropped.getName(),
                    List.of(percentTeamPlayed[0], percentTeamPlayed[1]),
                    List.of(percentDivisionPlayed[0], percentDivisionPlayed[1]));
        } else
            return null;
    }

    private int insertAndGetID(Member captain, Role divRole, String teamName) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO drop_requests(captainId, divRoleId, teamName)
                    VALUES (?, ?, ?);""");
            ps.setLong(1, captain.getIdLong());
            ps.setLong(2, divRole.getIdLong());
            ps.setString(3, teamName);
            ps.execute();

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM drop_requests;");

            return rs.getInt(1);
        }
    }

    public boolean hasActiveDropRequest(String teamName) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT (SELECT id FROM drop_requests WHERE active = true AND teamName = ?) IS NOT NULL;""");
            ps.setString(1, teamName);
            ResultSet rs = ps.executeQuery();

            return rs.getBoolean(1);
        }
    }

    public Map<Role, Division> getDivisions() {
        return divisions;
    }

    public void createDropRequest(ButtonInteractionEvent event) {
        Role divRole = event.getMember().getRoles().stream().filter(r -> r.getName().contains("Division")).findFirst()
                .orElse(null);
        Role orgaRole = event.getGuild().getRoles().stream().filter(r -> r.getName().contains("Turnierleitung"))
                .findFirst().orElse(null);
        MatchResult matchResult = SunnyUtils.CAPTAIN_NAME_REGEX.find(event.getMember().getNickname(), 0);

        if (matchResult == null) {
            event.editMessage("Ich kann nicht herausfinden, für welches Team du spielst. Vorgang abgebrochen.")
                    .setReplace(true)
                    .queue();
        } else if (orgaRole == null) {
            event.editMessage("Ich kann die Rolle der Turnierleiter nicht finden. Vorgang abgebrochen.")
                    .setReplace(true)
                    .queue();
        } else {
            String teamName = matchResult.getGroupValues().get(1);
            try {
                TextChannel dropRequestsChannel = DiscordBot.getDSBGuild()
                        .getTextChannelById(SunnySettings.DROP_REQUEST.aLong("channel"));
                DropRequest dr;
                if (dropRequestsChannel != null && dropRequestsChannel.canTalk()
                        && (dr = createDropRequest(divRole, teamName, event.getMember())) != null) {
                    double percentTeamPlayed = (dr.gamesTeamPlayed().get(0) * 100)
                            / (double) dr.gamesTeamPlayed().get(1);
                    double percentDivisionPlayed = (dr.gamesDivisionPlayed().get(0) * 100)
                            / (double) dr.gamesDivisionPlayed().get(1);

                    EmbedBuilder eb = Embeds.byRole(dr.divisionRole())
                            .setAuthor(
                                    String.format("%s möchte aus der %s droppen.", dr.teamName(),
                                            dr.divisionRole().getName()),
                                    null,
                                    "https://cdn.discordapp.com/emojis/998387086019272774.webp?size=96&quality=lossless")
                            .addField("Gespielte Spiele als Team:",
                                    String.format("%d / %d (%.1f%%)", dr.gamesTeamPlayed().get(0),
                                            dr.gamesTeamPlayed().get(1), percentTeamPlayed),
                                    true)
                            .addField("Gespielte Spiele in der Division:",
                                    String.format("%d / %d (%.1f%%)", dr.gamesDivisionPlayed().get(0),
                                            dr.gamesDivisionPlayed().get(1), percentDivisionPlayed),
                                    true)
                            .setFooter(String.format("Drop-Request ID: %d • Drop requested by: %s", dr.id(),
                                    dr.captain().getEffectiveName()));

                    Button reject = Button
                            .danger(String.format("&%d:drop:%d:0", orgaRole.getIdLong(), dr.id()), "Drop ablehnen")
                            .asEnabled();
                    Button drop0 = Button.success(String.format("&%d:drop:%d:1", orgaRole.getIdLong(), dr.id()),
                            "Droppen und Spiele annullieren").asEnabled();
                    Button drop1 = Button.success(String.format("&%d:drop:%d:2", orgaRole.getIdLong(), dr.id()),
                            "Droppen und restliche Spiele 0-5 werten").asEnabled();
                    dropRequestsChannel.sendMessageEmbeds(eb.build())
                            .setComponents(ActionRow.of(reject, drop0, drop1))
                            .queue(s -> event.editMessage("Dein Drop-Request ist nun bei den TOs angekommen.")
                                    .setReplace(true)
                                    .queue(),
                                    failure -> {
                                        event.editMessage(String.format(
                                                "Ein Fehler ist aufgetreten. Dein Drop-Request wurde nicht abgesendet. Melde dich bitte beim %s",
                                                ChannelReferences.HELPDESK))
                                                .setReplace(true)
                                                .queue();
                                        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                                            PreparedStatement ps = conn
                                                    .prepareStatement("DELETE FROM drop_requests WHERE id = ?;");
                                            ps.setInt(1, dr.id());
                                            ps.execute();
                                        } catch (SQLException ex) {
                                            LOG.error("Another error occured, couldn't remove Drop-Request: ", ex);
                                        }
                                    });
                } else {
                    event.editMessage(
                            "Dein Drop-Request wurde nicht abgeschickt. Entweder fehlen mir Berechtigungen, oder es gibt bereits ein Drop-Request.")
                            .setReplace(true)
                            .queue();
                }
            } catch (DataAccessException | SQLException e) {
                event.editMessage("Ein Fehler ist aufgetreten.")
                        .setReplace(true)
                        .queue();
                LOG.error("Coudn't handle drop request: ", e);
            }
        }
    }

    public void dropTeam(int id, String dropAction, User user) throws Exception {
        DropRequest dr = null;
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT * FROM drop_requests
                    WHERE id = ?;""");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Member captain = DiscordBot.getDSBGuild().retrieveMember(UserSnowflake.fromId(rs.getLong(2)))
                        .complete();
                Role divRole = DiscordBot.getDSBGuild().getRoleById(rs.getLong(3));
                String teamName = rs.getString(4);

                ps = conn.prepareStatement("UPDATE drop_requests SET active = false WHERE id = ?;");
                ps.setInt(1, id);
                ps.execute();

                dr = new DropRequest(id, captain, divRole, teamName, null, null);
            }
        }

        if (dr != null) {
            dropTeam(dr.teamName(), dr.divisionRole(), dropAction);
            Guild dsbGuild = DiscordBot.getDSBGuild();
            Member self = DiscordBot.getDSBGuild().getSelfMember();

            if (dr.captain() != null && self.canInteract(dr.captain()) && self.canInteract(dr.divisionRole())) {
                dsbGuild.removeRoleFromMember(dr.captain(), dr.divisionRole()).queue();
                dr.captain().modifyNickname(null).queue();

                dr.captain().getUser().openPrivateChannel().queue(s -> {
                    MessageEmbed meInform;
                    if ("reject".equals(dropAction)) {
                        meInform = Embeds.error("Dein Drop-Antrag wurde abgelehnt.")
                                .setDescription("Bitte melde dich beim " + ChannelReferences.HELPDESK.getAsMention()
                                        + " und bespreche es dort weiter.")
                                .setFooter("Drop-Antrag bearbeitet von: " + user.getName())
                                .build();
                    } else {
                        meInform = Embeds.colored(new Color(0x14FF28))
                                .setAuthor("Dein Team wurde erfolgreich gedropped!")
                                .setDescription(
                                        "Schade, dass ihr euch entschieden habt, zu droppen. Wir hoffen trotzdem, dass ihr weiterhin Spaß mit der DSB haben werdet!")
                                .setFooter("Drop-Antrag bearbeitet von: " + user.getName())
                                .build();
                    }
                    s.sendMessage(new MessageCreateBuilder()
                            .setContent("*Du bekommst diese Nachricht, weil du Kapitän(in) eines Teams bist.*")
                            .setEmbeds(meInform)
                            .build()).queue();
                });
            }
        }
    }
}
