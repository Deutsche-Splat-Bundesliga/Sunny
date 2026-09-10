package dsb.sunny.cocap;

import dsb.sunny.DiscordBot;
import dsb.sunny.utils.SunnyUtils;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.UserSnowflake;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CoCaptainModule {

    public CoCaptainModule() {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            conn.createStatement().execute("""
                    CREATE TABLE IF NOT EXISTS CoCaptain (
                        MainCaptain INTEGER NOT NULL,
                        CoCaptain INTEGER NOT NULL UNIQUE,
                        PRIMARY KEY (MainCaptain)
                    );""");
        } catch (SQLException ex) {
            LoggerFactory.getLogger(CoCaptainModule.class).warn("Couldn't create table!", ex);
        }
    }

    public boolean makeCoCaptain(Member mainCaptain, Member coCaptain) throws SQLException {
        boolean b = removeCoCap(mainCaptain);

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    INSERT OR REPLACE INTO CoCaptain VALUES (?, ?);""");
            ps.setLong(1, mainCaptain.getIdLong());
            ps.setLong(2, coCaptain.getIdLong());
            ps.execute();

            Guild guild = coCaptain.getGuild();
            Member selfMember = guild.getSelfMember();
            if (selfMember.canInteract(coCaptain)) {
                String teamName = SunnyUtils.CAPTAIN_NAME_REGEX.matchEntire(mainCaptain.getEffectiveName()).getGroupValues().get(1);
                String newNickname = String.format("[%s] %s", teamName, coCaptain.getEffectiveName());
                if (newNickname.length() >= 32) {
                    newNickname = String.format("[%s]", teamName);
                }
                coCaptain.modifyNickname(newNickname).queue();

                for (Role r : mainCaptain.getRoles()) {
                    if (r.getName().contains("Division") && selfMember.canInteract(r)) {
                        guild.addRoleToMember(coCaptain, r).queue();
                    }
                }
            }
        }

        return b;
    }

    public boolean removeCoCap(Member mainCaptain) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT CoCaptain.CoCaptain FROM CoCaptain
                    WHERE MainCaptain = ?;""");
            ps.setLong(1, mainCaptain.getIdLong());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                DiscordBot.getDSBGuild().retrieveMember(UserSnowflake.fromId(rs.getLong(1))).queue(member -> {
                    if (member.getGuild().getSelfMember().canInteract(member)) {
                        member.modifyNickname(null).queue();

                        for (Role r : member.getRoles()) {
                            if (r.getName().contains("Division") && member.getGuild().getSelfMember().canInteract(r)) {
                                member.getGuild().removeRoleFromMember(member, r).queue();
                            }
                        }
                    }
                });

                PreparedStatement removePs = conn.prepareStatement("""
                        DELETE FROM CoCaptain WHERE MainCaptain = ?;""");
                removePs.setLong(1, mainCaptain.getIdLong());
                removePs.execute();

                return true;
            }
        }
        return false;
    }

    public boolean isCoCaptain(Member member) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT CoCaptain.CoCaptain FROM CoCaptain WHERE CoCaptain = ?""");
            ps.setLong(1, member.getIdLong());
            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }

    public boolean hasCoCaptainSet(Member mainCaptain) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    SELECT CoCaptain.MainCaptain FROM CoCaptain WHERE MainCaptain = ?""");
            ps.setLong(1, mainCaptain.getIdLong());
            ResultSet rs = ps.executeQuery();

            return rs.next();
        }
    }
}
