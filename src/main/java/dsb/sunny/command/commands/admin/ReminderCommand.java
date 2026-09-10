package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.reminder.Reminder;
import dsb.sunny.utils.SunnyUtils;
import kotlin.text.MatchResult;
import kotlin.text.Regex;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.time.*;
import java.util.List;

public class ReminderCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        String sub = event.getSubcommandName();
        switch (sub) {
            case "add" -> handleAdd(event);
            case "remove" -> handleRemove(event);
        }
    }

    private void handleAdd(SlashCommandInteractionEvent event) {
        boolean isRelative = false;
        OptionMapping mapping = event.getOption("time");
        String content = event.getOption("content", OptionMapping::getAsString);

        if (mapping == null) {
            mapping = event.getOption("relative");
            isRelative = true;
        }

        if (mapping == null) {
            event.reply("Bitte wähle zu den Argumenten entweder **relative** oder **time**.")
                    .setEphemeral(true)
                    .queue();
        } else if (isRelative) {
            String[] input = mapping.getAsString().split("\\s+");
            Regex pattern = SunnyUtils.RELATIVE_TIME_REGEX;
            ZonedDateTime time = ZonedDateTime.now().withNano(0).withSecond(0);
            for (String s : input) {
                if (!pattern.matches(s)) {
                    event.reply("Deine Zeitangabe enthält Fehler. Bitte überprüfe deine Angabe.").setEphemeral(true).queue();
                    return;
                } else {
                    MatchResult mr = pattern.matchEntire(s);
                    long quantity = Long.parseLong(mr.getGroupValues().get(1));
                    char identifier = mr.getGroupValues().get(2).charAt(0);
                    time = switch (identifier) {
                        case 'w' -> time.plus(Duration.ofDays(quantity * 7));
                        case 'd' -> time.plus(Duration.ofDays(quantity));
                        case 'h' -> time.plus(Duration.ofHours(quantity));
                        case 'm' -> time.plus(Duration.ofMinutes(quantity));
                        case 's' -> time.plus(Duration.ofSeconds(quantity));
                        default -> time;
                    };
                }
            }
            Reminder newReminder = new Reminder(event.getMember(), time.toInstant(), content);

            if (DiscordBot.getReminder().addReminder(newReminder)) {
                newReminder.insert();
                event.reply("Diese Erinnerung wurde erfolgreich hinzugefügt!")
                        .addEmbeds(newReminder.getAsEmbed())
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("Du hast zu der Zeit bereits eine Erinnerung. Bitte lösche deine vorherige und dann versuche es erneut.")
                        .queue();
            }
        } else {
            String input = mapping.getAsString();
            Regex patttern = SunnyUtils.TIME_REGEX;
            if (!patttern.matches(input)) {
                event.reply("Deine Zeitangabe enthält Fehler. Bitte überprüfe deine Angabe.").setEphemeral(true).queue();
            } else {
                ZonedDateTime time = ZonedDateTime.now(ZoneId.systemDefault());
                String[] split1 = input.split(" ");
                try {
                    time = time.withDayOfMonth(Integer.parseInt(split1[0].split("\\.")[0]))
                            .withMonth(Integer.parseInt(split1[0].split("\\.")[1]))
                            .withYear(Integer.parseInt(split1[0].split("\\.")[2]))
                            .withHour(Integer.parseInt(split1[1].split(":")[0]))
                            .withMinute(Integer.parseInt(split1[0].split("\\.")[1]))
                            .withSecond(0)
                            .withNano(0);
                } catch (NumberFormatException | DateTimeException e) {
                    event.reply("Deine Zeitangabe enthält Fehler. Bitte überprüfe deine Angabe.").setEphemeral(true).queue();
                    return;
                }

                Reminder newReminder = new Reminder(event.getMember(), time.toInstant(), content);

                if (time.isBefore(ZonedDateTime.now())) {
                    event.reply("Die angegebene Zeit liegt in der Vergangenheit.")
                            .setEphemeral(true)
                            .queue();
                } else if (!DiscordBot.getReminder().addReminder(newReminder)) {
                    event.reply("Du hast zu der Zeit bereits eine Erinnerung. Bitte lösche deine vorherige und dann versuche es erneut.")
                            .setEphemeral(true)
                            .queue();
                } else {
                    newReminder.insert();
                    event.reply("Diese Erinnerung wurde erfolgreich hinzugefügt!")
                            .addEmbeds(newReminder.getAsEmbed())
                            .setEphemeral(true)
                            .queue();
                }
            }
        }
    }

    private void handleRemove(SlashCommandInteractionEvent event) {
        List<Reminder> reminders = DiscordBot.getReminder().getRemindersOfUser(event.getMember());
        StringSelectMenu.Builder menu = StringSelectMenu.create("REMINDME")
                .setMaxValues(1)
                .setMinValues(1);

        if (reminders.isEmpty()) {
            event.reply("Du hast keine Erinnerungen gesetzt.").setEphemeral(true).queue();
        } else {
            for (Reminder reminder : reminders) {
                Duration d = Duration.between(Instant.now(), reminder.time());
                String s = "in %s: %s".formatted(
                        d.toDays() > 0 ? "%dT".formatted(d.toDays()) : d.toHours() > 0 ? "%dh".formatted(d.toHours()) : "<1h",
                        reminder.content()
                );
                menu.addOption(s, String.valueOf(reminder.time().getEpochSecond()));
            }

            event.reply("Bitte wähle aus, welche Erinnerungen du löschen möchtest.")
                    .addComponents(ActionRow.of(menu.build()))
                    .setEphemeral(true)
                    .queue();
        }
    }


    @Override
    public CommandData commandData() {
        return Commands.slash("remindme", "Richte Erinnerungen ein")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(new SubcommandData("add", "Füge eine neue Erinnerung hinzu")
                        .addOption(OptionType.STRING, "content", "Der Inhalt deiner Erinnerung", true)
                        .addOption(OptionType.STRING, "time", "Die Zeitangabe der Erinnerung, formatiert in \"DD.MM.YYYY HH:MM\"")
                        .addOption(OptionType.STRING, "relative", "Eine relative Zeitangabe, formatiert in \"(Zahl)(Buchstabe)\", getrennt in Leerzeichen"))
                .addSubcommands(new SubcommandData("remove", "Zeige eine Liste von Erinnerungen an und lösche sie."));
    }
}
