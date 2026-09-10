package dsb.sunny.challonge;

import net.dv8tion.jda.api.entities.Role;

public record Division(Role divisionRole, String challongeID) {

    public String challongeID() {
        return challongeID;
    }

    public Role divisionRole() {
        return divisionRole;
    }
}
