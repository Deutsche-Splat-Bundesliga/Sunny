package dsb.sunny.reminder;

import dsb.sunny.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.UserSnowflake;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class Reminder {
    private final UserSnowflake snowflake;
    private final Instant time;
    private final String content;
    private ScheduledFuture<?> task;

    public Reminder(UserSnowflake snowflake, Instant time, String content) {
        this.snowflake = snowflake;
        this.time = time;
        this.content = content;
    }

    public MessageEmbed getAsEmbed() {
        return new EmbedBuilder()
                .setAuthor("Erinnerung")
                .setColor(new Color(0xEEFF00))
                .addField("Nutzer", snowflake.getAsMention(), true)
                .addField("Zeitpunkt", "<t:%d:F>".formatted(time.getEpochSecond()), true)
                .addField("Bezeichnung", content, false)
                .setFooter("Sunny (DSB) - Erinnerung")
                .build();
    }

    public void informUser() {
        DiscordBot.getDSBGuild().retrieveMember(snowflake).queue(member ->
                member.getUser().openPrivateChannel().queue(privateChannel ->
                        privateChannel.sendMessage("Hey %s, ich sollte dich an etwas erinnern!".formatted(snowflake.getAsMention()))
                                .addEmbeds(getAsEmbed())
                                .queue()));
    }

    public void insert() {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO Reminder VALUES (?, ?, ?)""");

            ps.setLong(1, snowflake.getIdLong());
            ps.setLong(2, time.getEpochSecond());
            ps.setString(3, content);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete() {
        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                    DELETE FROM Reminder WHERE UserSnowflake = ? AND Timestamp = ?;""");

            ps.setLong(1, snowflake.getIdLong());
            ps.setLong(2, time.getEpochSecond());
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long getDurationSeconds() {
        return Duration.between(Instant.now(), time).toSeconds();
    }

    public void scheduleReminder(ScheduledExecutorService service) {
        task = service.schedule(() -> {
            informUser();
            delete();
        }, getDurationSeconds(), TimeUnit.SECONDS);
    }

    public void cancelReminder() {
        if (task != null) {
            task.cancel(false);
        }
    }

    public UserSnowflake snowflake() {
        return snowflake;
    }

    public Instant time() {
        return time;
    }

    public String content() {
        return content;
    }

    public boolean isPast() {
        return time.isBefore(Instant.now());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Reminder) obj;
        return Objects.equals(this.snowflake, that.snowflake) &&
                Objects.equals(this.time, that.time) &&
                Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snowflake, time, content);
    }

    @Override
    public String toString() {
        return "Reminder[" +
                "snowflake=" + snowflake + ", " +
                "time=" + time + ", " +
                "content=" + content + ']';
    }
}
