package dsb.sunny.challonge;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;

public record DropRequest(Integer id, Member captain, Role divisionRole, String teamName, List<Integer> gamesTeamPlayed, List<Integer> gamesDivisionPlayed) {

    @Override
    public Integer id() {
        return id;
    }

    @Override
    public Member captain() {
        return captain;
    }

    @Override
    public Role divisionRole() {
        return divisionRole;
    }

    @Override
    public String teamName() {
        return teamName;
    }

    public List<Integer> gamesTeamPlayed() {
        return gamesTeamPlayed;
    }

    public List<Integer> gamesDivisionPlayed() {
        return gamesDivisionPlayed;
    }
}
