package dsb.sunny.settings;

import dsb.sunny.DiscordBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public enum SunnySettings {
    JOINROLE("joinrole"),
    LIVESTREAM("livestream"),
    SOCIAL_MEDIA("social_media"),
    SCORE_REPORT("score_report"),
    DROP_REQUEST("drop_request"),
    CHANGELOG("changelog"),
    MAPPOOL("mappool"),
    GENERAL("general"),
    CLEANER("cleaner"),
    SCENARIOS("scenarios"),
    CONSTELLATIONS("constellations");

    private final String type;
    private final HashMap<String, String> settings;
    private final Logger log = LoggerFactory.getLogger(SunnySettings.class);

    SunnySettings(String type) {
        HashMap<String, String> settings1;
        this.type = type;

        try {
            settings1 = getValues();
        } catch (SQLException e) {
            log.error("Couldn't get Settings for: " + name(), e);
            settings1 = null;
        }
        this.settings = settings1;
    }

    private HashMap<String, String> getValues() throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("SELECT name, value FROM Settings WHERE type = ?;");
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();

            HashMap<String, String> values = new HashMap<>();
            while (rs.next()) {
                String name = rs.getString(1);
                String value = rs.getString(2);
                values.put(name, value);
            }
            return values;
        }
    }

    public void setValue(String key, Object o) throws SQLException {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            String query = """
                INSERT OR REPLACE INTO Settings
                VALUES (?, ?, ?);
                """;
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, type);
            ps.setString(2, key);
            if (o == null) {
                ps.setString(3, null);
            } else if (o instanceof Color) {
                int r = ((Color) o).getRed();
                int g = ((Color) o).getGreen();
                int b = ((Color) o).getBlue();
                ps.setString(3, String.format("#%02x%02x%02x", r, g, b));
            } else if (o instanceof List<?>) {
                String list = ((List<?>) o).stream().map(s -> ":" + s).collect(Collectors.joining()).substring(1);
                ps.setString(3, list);
            } else if (o instanceof Guild) {
                ps.setLong(3, ((Guild) o).getIdLong());
            } else if (o instanceof GuildMessageChannel) {
                ps.setLong(3, ((GuildMessageChannel) o).getIdLong());
            } else {
                ps.setObject(3, o);
            }
            ps.execute();
            settings.put(key, o != null ? o.toString() : null);
        }
    }

    public List<String> stringList(String key) {
        String[] list = settings.get(key).split(":");
        return Arrays.asList(list);
    }

    public List<Integer> integerList(String key) {
        String value = settings.get(key);
        List<Integer> l = new LinkedList<>();

        if (value == null) {
            return l;
        }

        String[] list = value.split(":");
        for (String s : list) {
            if (!s.isEmpty()) l.add(Integer.parseInt(s));
        }
        return l;
    }

    public int integer(String key) {
        return Integer.parseInt(settings.get(key));
    }

    public String string(String key) {
        return settings.get(key);
    }

    public boolean aBoolean(String key) {
        return "1".equalsIgnoreCase(settings.get(key));
    }

    public long aLong(String key) {
        return Long.parseLong(settings.get(key));
    }

    public Color color(String key) {
        return Color.decode(settings.get(key));
    }

    public float aFloat(String key) {
        return Float.parseFloat(settings.get(key));
    }

    public double aDouble(String key) {
        return Double.parseDouble(settings.get(key));
    }

    public GuildMessageChannel guildMessageChannel(String key) {
        return (GuildMessageChannel) DiscordBot.getDSBGuild().getGuildChannelById(settings.get(key));
    }

    public List<Long> longList(String key) {
        String value = settings.get(key);
        List<Long> l = new LinkedList<>();

        if (value == null) {
            return l;
        }

        String[] list = value.split(":");
        for (String s : list) {
            if (!s.isEmpty()) l.add(Long.parseLong(s));
        }
        return l;
    }
}