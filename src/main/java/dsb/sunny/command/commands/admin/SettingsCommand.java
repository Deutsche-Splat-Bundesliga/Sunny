package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Locale;

public class SettingsCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception { // FIXME: 14.10.2022 Fix NPE
        if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Du hast keine Berechtigungen, diesen Command auszuführen.").setEphemeral(true).queue();
        } else {
            String[] type = event.getOption("type").getAsString().split(":");
            String value = event.getOption("value") != null ? event.getOption("value").getAsString() : null;
            switch (SunnySettings.valueOf(type[0].toUpperCase(Locale.ROOT))) {
                case JOINROLE -> {
                    switch (type[1]) {
                        case "role" -> {
                            if (value == null) {
                                event.reply(String.format("Die Join-Rolle ist momentan %s.", event.getGuild()
                                        .getRoleById(SunnySettings.JOINROLE.aLong("role")).getAsMention()))
                                        .setEphemeral(true).queue();
                            } else if (event.getGuild().getRoleById(value) == null) {
                                event.reply("Dieser Wert ist keine ID einer Rolle.").setEphemeral(true).queue();
                            } else {
                                SunnySettings.JOINROLE.setValue("role", value);
                                event.reply("Diese Rolle wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                            }
                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                case LIVESTREAM -> {
                    switch (type[1]) {
                        case "channel" -> {
                            if (value == null) {
                                event.reply(String.format("Die Livestreams werden auf dem Channel %s angekündigt.",
                                        event.getGuild().getGuildChannelById(SunnySettings.LIVESTREAM.aLong("channel"))
                                                .getAsMention()))
                                        .setEphemeral(true).queue();
                            } else if (event.getGuild().getGuildChannelById(value) == null) {
                                event.reply("Dieser Wert ist keine ID eines Channels.").setEphemeral(true).queue();
                            } else {
                                SunnySettings.LIVESTREAM.setValue("channel", value);
                                event.reply("Dieser Channel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                            }
                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                case SOCIAL_MEDIA -> {
                    switch (type[1]) {
                        case "channel" -> {
                            if (value == null) {
                                event.reply(String
                                        .format("Die Socialmedia-Ankündigungen werden auf dem Channel %s angekündigt.",
                                                event.getGuild()
                                                        .getGuildChannelById(SunnySettings.LIVESTREAM.aLong("channel"))
                                                        .getAsMention()))
                                        .setEphemeral(true).queue();
                            } else if (event.getGuild().getGuildChannelById(value) == null) {
                                event.reply("Dieser Wert ist keine ID eines Channels.").setEphemeral(true).queue();
                            } else {
                                SunnySettings.SOCIAL_MEDIA.setValue("channel", value);
                                event.reply("Dieser Channel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                            }
                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                case SCORE_REPORT -> {
                    switch (type[1]) {
                        case "channel" -> {
                            if (value == null) {
                                event.reply(String.format("Die Scores werden bei %s angezeigt.",
                                        event.getGuild()
                                                .getGuildChannelById(SunnySettings.SCORE_REPORT.aLong("channel"))
                                                .getAsMention()))
                                        .setEphemeral(true).queue();
                            } else if (event.getGuild().getGuildChannelById(value) == null) {
                                event.reply("Dieser Wert ist keine ID eines Channels.").setEphemeral(true).queue();
                            } else {
                                SunnySettings.SCORE_REPORT.setValue("channel", value);
                                event.reply("Dieser Channel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                            }

                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                case DROP_REQUEST -> {
                    switch (type[1]) {
                        case "channel" -> {
                            if (value == null) {
                                event.reply(String.format("Die Drops werden bei %s angezeigt.",
                                        event.getGuild()
                                                .getGuildChannelById(SunnySettings.DROP_REQUEST.aLong("channel"))
                                                .getAsMention()))
                                        .setEphemeral(true).queue();
                            } else if (event.getGuild().getGuildChannelById(value) == null) {
                                event.reply("Dieser Wert ist keine ID eines Channels.").setEphemeral(true).queue();
                            } else {
                                SunnySettings.DROP_REQUEST.setValue("channel", value);
                                event.reply("Dieser Channel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                            }
                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                case CHANGELOG -> {
                    switch (type[1]) {
                        case "channel" -> {
                            if (value == null) {
                                event.reply(String.format("Bot-Commands werden bei %s angezeigt.", event.getGuild()
                                        .getGuildChannelById(SunnySettings.CHANGELOG.aLong("channel")).getAsMention()))
                                        .setEphemeral(true).queue();
                            } else if (event.getGuild().getGuildChannelById(value) == null) {
                                event.reply("Dieser Wert ist keine ID eines Channels.").setEphemeral(true).queue();
                            } else {
                                SunnySettings.CHANGELOG.setValue("channel", value);
                                event.reply("Dieser Channel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                            }
                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                case MAPPOOL -> {
                    switch (type[1]) {
                        case "channel" -> {
                            if (value == null) {
                                event.reply(String.format("Der jetzige Mappool heißt \"%s\"",
                                        SunnySettings.MAPPOOL.string("title"))).setEphemeral(true).queue();
                            }
                            DiscordBot.getMapListGenerator().setTitle(value);
                            event.reply("Dieser Titel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                case GENERAL -> {
                    switch (type[1]) {
                        case "color" -> {
                            if (value == null) {
                                event.reply(String.format("Die jetzige Farbe hat den HEX-Code %s",
                                        SunnySettings.GENERAL.string("color"))).setEphemeral(true).queue();
                            } else {
                                try {
                                    Color c = Color.decode(value);
                                    SunnySettings.GENERAL.setValue("color", c);
                                    event.reply("Diese Farbe wurde erfolgreich eingestellt!").setEphemeral(true)
                                            .queue();
                                } catch (NumberFormatException ex) {
                                    event.reply("Das ist kein Hex-Code.").setEphemeral(true).queue();
                                }
                            }
                        }
                        default ->
                            event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                    }
                }
                default -> {
                    event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
                }
            }
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("settings", "Ändere Einstellungen bei Sunny, oder schaue sie dir an.")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addOptions(
                        new OptionData(OptionType.STRING, "type", "Der Typ, welche Einstellung du ändern willst.", true)
                                .addChoice("Joinrole (ID)", "joinrole:role")
                                .addChoice("Livestream-Channel (ID)", "livestream:channel")
                                .addChoice("Socialmedia-Channel (ID)", "social_media:channel")
                                .addChoice("Score-Report-Channel (ID)", "score_report:channel")
                                .addChoice("Drop-Requests-Channel (ID)", "drop_request:channel")
                                .addChoice("Changelog-Channel (ID)", "changelog:channel")
                                .addChoice("Mappool-Titel (Name)", "mappool:title")
                                .addChoice("Farbe (HEX-Code)", "general:color"))
                .addOption(OptionType.STRING, "value", "Der Wert, der gesetzt werden soll.");
    }
}
