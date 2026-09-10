package dsb.sunny.nowrite;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

public record NoWriteRecord(Member member, Role role) {

    @Override
    public Role role() {
        return role;
    }

    @Override
    public Member member() {
        return member;
    }
}
