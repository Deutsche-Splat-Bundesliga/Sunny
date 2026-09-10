package dsb.sunny.channelcleaner;

import dsb.sunny.DiscordBot;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChannelCleaner {

    private final Guild guild;
    private final Map<Long, List<Long>> map = new HashMap<>();
    private final List<Long> roles;

    public ChannelCleaner(Guild guild) {
        this.guild = guild;
        this.roles = new LinkedList<>(SunnySettings.CLEANER.longList("roles"));
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM ChannelCleanerChannels;");

            while (rs.next()) {
                map.put(rs.getLong(1), new LinkedList<>());
            }

            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM [ChannelCleaner];");

            while (rs.next()) {
                map.getOrDefault(rs.getLong(1), new LinkedList<>()).add(rs.getLong(2));
            }
        } catch (SQLException ex) {
            LoggerFactory.getLogger(getClass()).error("Couldn't load channels!", ex);
        }
    }

    public void clearChannels() {
        List<GuildMessageChannel> channels = new LinkedList<>();
        List<Role> roles = new LinkedList<>();

        for (Long aLong : map.keySet()) {
            channels.add((GuildMessageChannel) guild.getGuildChannelById(aLong));
        }

        for (Long role : this.roles) {
            roles.add(guild.getRoleById(role));
        }

        for (GuildMessageChannel channel : channels) {
            if (channel != null) {
                channel.getHistoryFromBeginning(100).queue(messageHistory -> {
                    List<Long> keptMessages = map.get(channel.getIdLong());
                    for (Message message : messageHistory.getRetrievedHistory()) {
                        if (!keptMessages.contains(message.getIdLong())) {
                            message.delete().queue();
                        }
                    }
                });
            }
        }

        for (Role role : roles) {
            if (role != null) {
                guild.findMembers(member -> member.getRoles().contains(role)).onSuccess(members -> {
                    for (Member member : members) {
                        if (guild.getSelfMember().canInteract(member)) {
                            guild.removeRoleFromMember(member, role).queue();
                        }
                    }
                });
            }
        }
    }

    public boolean addMessageToChannel(GuildMessageChannel channel, long message) throws SQLException {
        List<Long> list = map.get(channel.getIdLong());
        if (list == null) {
            addChannel(channel);
            list = map.get(channel.getIdLong());
        }

        if (!list.contains(message)) {
            try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
                        INSERT INTO ChannelCleanerMessages VALUES (?, ?);""");
                ps.setLong(1, channel.getIdLong());
                ps.setLong(2, message);
                ps.execute();
            }
            return list.add(message); // true
        }
        return false;
    }

    public boolean removeMessageFromChannel(GuildMessageChannel channel, long message) throws SQLException {
        boolean b = map.getOrDefault(channel.getIdLong(), new LinkedList<>()).remove(message);
        if (b) {
            try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
                        DELETE FROM ChannelCleanerMessages WHERE ChannelID = ? AND MessageID = ?;""");
                ps.setLong(1, channel.getIdLong());
                ps.setLong(2, message);
                ps.execute();
            }
        }
        return b;
    }

    public boolean addChannel(GuildMessageChannel channel) throws SQLException {
        boolean b = map.putIfAbsent(channel.getIdLong(), new LinkedList<>()) == null;
        if (b) {
            try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
                        INSERT INTO ChannelCleanerChannels VALUES (?);""");
                ps.setLong(1, channel.getIdLong());
                ps.execute();
            }
        }
        return b;
    }

    public boolean removeChannel(GuildMessageChannel channel) throws SQLException {
        boolean b = map.remove(channel.getIdLong()) != null;
        if (b) {
            try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
                        DELETE FROM ChannelCleanerChannels WHERE ChannelID = ?;""");
                ps.setLong(1, channel.getIdLong());
                ps.execute();
            }
        }
        return b;
    }

    public boolean addRole(Role role) throws SQLException {
        boolean b = !roles.contains(role.getIdLong());
        if (b) {
            roles.add(role.getIdLong());
            SunnySettings.CLEANER.setValue("roles", roles);
        }
        return b;
    }

    public boolean removeRole(Role role) throws SQLException {
        boolean b = roles.remove(role.getIdLong());
        if (b) {
            SunnySettings.CLEANER.setValue("roles", roles);
        }
        return b;
    }
}
