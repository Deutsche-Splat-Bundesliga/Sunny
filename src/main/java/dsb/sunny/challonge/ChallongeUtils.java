package dsb.sunny.challonge;

import at.stefangeyer.challonge.model.Match;
import at.stefangeyer.challonge.model.Participant;
import at.stefangeyer.challonge.model.Tournament;
import kotlin.text.Regex;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChallongeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ChallongeUtils.class);

    public static Regex SCORE_REGEX = new Regex("(?<Score1>[012345]|\\bff\\b)[-:](?<Score2>[012345]|\\bff\\b)");

    public static Participant getParticipant(String participantName, List<Participant> allParticipants) {
        Participant currentParticipant = null;
        int currentHighestScore = 0;
        boolean multiplePossible = false;
        for (Participant p : allParticipants) {
            int score = FuzzySearch.weightedRatio(participantName, p.getName());
            if (score == 100) {
                LOG.info("Insta-polled participant {}, score = {}", p.getName(), score);
                return p;
            } else if (currentParticipant == null && score >= 90) {
                currentParticipant = p;
                currentHighestScore = score;
            } else if (currentParticipant != null && score == currentHighestScore) {
                multiplePossible = true;
            } else if (currentParticipant != null && score > currentHighestScore) {
                currentParticipant = p;
                currentHighestScore = score;
                multiplePossible = false;
            }
        }
        LOG.info("Polled participant: {} for input {} with a score of {}. multiplePossible = {}", currentParticipant != null ? currentParticipant.getName() : null, participantName, currentHighestScore, multiplePossible);
        return multiplePossible ? null : currentParticipant;
    }

    public static List<Match> getMatches(Participant participant, List<Match> allMatches) {
        List<Match> participantMatches = new ArrayList<>();
        for (Match m : allMatches) {
            Long id = participant.getId();
            Long groupPlayerId = participant.getGroupPlayerIds().isEmpty() ? null : participant.getGroupPlayerIds().get(0);
            if (Objects.equals(id, m.getPlayer1Id()) || Objects.equals(groupPlayerId, m.getPlayer1Id())
                    || Objects.equals(id, m.getPlayer2Id()) || Objects.equals(groupPlayerId, m.getPlayer2Id())) {
                participantMatches.add(m);
            }
        }
        return participantMatches;
    }

    public static List<Match> getMatches(Participant participant, List<Match> allMatches, boolean playoffs) {
        List<Match> participantMatches = new ArrayList<>();
        for (Match m : allMatches) {
            if (playoffs) {
                Long id = participant.getId();
                if (m.getGroupId() == null && (id.equals(m.getPlayer1Id()) || id.equals(m.getPlayer2Id()))) {
                    participantMatches.add(m);
                }
            } else {
                Long groupPlayerId = participant.getGroupPlayerIds().isEmpty() ? null : participant.getGroupPlayerIds().get(0);
                if (m.getGroupId() != null && (m.getPlayer1Id().equals(groupPlayerId) || m.getPlayer2Id().equals(groupPlayerId))) {
                    participantMatches.add(m);
                }
            }
        }
        return participantMatches;
    }

    public static boolean hasPlayoffMatches(Tournament bracket) {
        for (Match m : bracket.getMatches()) {
            if (m.getGroupId() == null) {
                return true;
            }
        }
        return false;
    }

    public static Match getActiveMatch(List<Match> participantMatches, Participant opponent, boolean playoff) {
        for (Match m : participantMatches) {
            if (playoff) {
                if (m.getPlayer1Id().equals(opponent.getId()) || m.getPlayer2Id().equals(opponent.getId())) return m;
            } else if (opponent.getGroupPlayerIds().isEmpty()) {
                return null;
            } else {
                long opponentId = opponent.getGroupPlayerIds().get(0);
                if (m.getPlayer1Id().equals(opponentId) || m.getPlayer2Id().equals(opponentId)) return m;
            }
        }
        return null;
    }
}
