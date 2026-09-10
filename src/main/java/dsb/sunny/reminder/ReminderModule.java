package dsb.sunny.reminder;

import dsb.sunny.DiscordBot;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReminderModule extends ListenerAdapter {
    private final List<Reminder> reminders;
    private final ScheduledExecutorService service;

    public ReminderModule() {
        this.reminders = new ArrayList<>();
        this.service = Executors.newScheduledThreadPool(1);

        try (Connection conn = DiscordBot.borrowDatabaseConnection()) {
            Statement stmt = conn.createStatement();
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS Reminder(
                         UserSnowflake INTEGER NOT NULL,
                         Timestamp INTEGER NOT NULL,
                         Content TEXT NOT NULL,
                     
                         PRIMARY KEY (UserSnowflake, Timestamp)
                    );""");

            ResultSet rs = conn.createStatement().executeQuery("""
                    SELECT * FROM Reminder;""");

            while (rs.next()) {
                UserSnowflake snowflake = UserSnowflake.fromId(rs.getLong(1));
                Instant timestamp = Instant.ofEpochSecond(rs.getLong(2));
                String content = rs.getString(3);

                Reminder reminder = new Reminder(snowflake, timestamp, content);
                if (reminder.isPast()) {
                    reminder.informUser();
                    reminder.delete();
                } else {
                    addReminder(reminder);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        service.scheduleAtFixedRate(() -> {
            for (Reminder reminder : List.copyOf(reminders)) {
                if (reminder.isPast()) {
                    reminder.delete();
                    reminders.remove(reminder);
                }
            }
        }, 15, 15, TimeUnit.MINUTES);
    }

    public boolean addReminder(Reminder reminder) {
        if (reminders.stream()
                .filter(r -> r.snowflake().equals(reminder.snowflake()))
                .anyMatch(r -> r.time().equals(reminder.time()))) {
            return false;
        }

        reminders.add(reminder);
        reminder.scheduleReminder(service);
        return true;
    }

    public boolean removeReminder(Reminder reminder) {
        if (reminders.remove(reminder)) {
            reminder.cancelReminder();
            reminder.delete();
            return true;
        }
        return false;
    }

    public Reminder getReminder(UserSnowflake snowflake, Instant instant) {
        return reminders.stream()
                .filter(r -> r.time().equals(instant) && r.snowflake().equals(snowflake))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        if (!event.getInteraction().getComponentId().startsWith("REMINDME")) return;

        Instant instant = Instant.ofEpochSecond(Long.parseLong(event.getInteraction().getSelectedOptions().get(0).getValue()));
        Reminder reminder = getReminder(event.getMember(), instant);
        if (reminder != null) {
            removeReminder(reminder);
        }

        List<Reminder> reminders = getRemindersOfUser(event.getMember());
        StringSelectMenu.Builder menu = StringSelectMenu.create("REMINDME")
                .setMaxValues(1)
                .setMinValues(1);

        if (reminders.isEmpty()) {
            event.editMessage("Du hast keine Erinnerungen mehr gesetzt.")
                    .setReplace(true)
                    .queue();
        } else {
            for (Reminder r : reminders) {
                Duration d = Duration.between(Instant.now(), r.time());
                String s = "in %s: %s".formatted(
                        d.toDays() > 0 ? "%d Tag%s".formatted(d.toDays(), d.toDays() == 1 ? "" : "e") : d.toHours() > 0 ? "%dh".formatted(d.toHours()) : "<1h",
                        r.content()
                );
                menu.addOption(s, String.valueOf(r.time().getEpochSecond()));
            }

            event.editMessage("Bitte wähle aus, welche Erinnerungen du löschen möchtest.")
                    .setComponents(ActionRow.of(menu.build()))
                    .setReplace(true)
                    .queue();
        }
    }

    public List<Reminder> getRemindersOfUser(UserSnowflake member) {
        return reminders.stream().filter(r -> r.snowflake().equals(member) && !r.isPast())
                .toList();
    }
}
