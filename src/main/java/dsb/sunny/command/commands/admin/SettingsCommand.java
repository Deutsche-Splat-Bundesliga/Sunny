package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.settings.SunnySettings;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IMentionable;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Locale;

public class SettingsCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        Member member = event.getMember();
        if (member == null || !member.getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Du hast keine Berechtigungen, diesen Command auszuführen.").setEphemeral(true).queue();
            return;
        }

        // Guaranteed non-null because a member is only present for guild interactions.
        Guild guild = event.getGuild();
        String[] type = event.getOption("type", "", OptionMapping::getAsString).split(":");
        String value = event.getOption("value", null, OptionMapping::getAsString);

        SunnySettings setting;
        try {
            setting = type.length < 2 ? null : SunnySettings.valueOf(type[0].toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            setting = null;
        }
        if (setting == null) {
            notImplemented(event);
            return;
        }

        switch (setting) {
            case JOINROLE -> {
                if (!type[1].equals("role")) {
                    notImplemented(event);
                } else if (value == null) {
                    event.reply(String.format("Die Join-Rolle ist momentan %s.",
                            mentionOf(role(guild, SunnySettings.JOINROLE.string("role")))))
                            .setEphemeral(true).queue();
                } else if (role(guild, value) == null) {
                    event.reply("Dieser Wert ist keine ID einer Rolle.").setEphemeral(true).queue();
                } else {
                    SunnySettings.JOINROLE.setValue("role", value);
                    event.reply("Diese Rolle wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                }
            }
            case LIVESTREAM -> channelSetting(event, guild, SunnySettings.LIVESTREAM, type,
                    "Die Livestreams werden auf dem Channel %s angekündigt.");
            case SOCIAL_MEDIA -> channelSetting(event, guild, SunnySettings.SOCIAL_MEDIA, type,
                    "Die Socialmedia-Ankündigungen werden auf dem Channel %s angekündigt.");
            case SCORE_REPORT -> channelSetting(event, guild, SunnySettings.SCORE_REPORT, type,
                    "Die Scores werden bei %s angezeigt.");
            case DROP_REQUEST -> channelSetting(event, guild, SunnySettings.DROP_REQUEST, type,
                    "Die Drops werden bei %s angezeigt.");
            case CHANGELOG -> channelSetting(event, guild, SunnySettings.CHANGELOG, type,
                    "Bot-Commands werden bei %s angezeigt.");
            case MAPPOOL -> {
                if (!type[1].equals("title")) {
                    notImplemented(event);
                } else if (value == null) {
                    event.reply(String.format("Der jetzige Mappool heißt \"%s\"",
                            SunnySettings.MAPPOOL.string("title"))).setEphemeral(true).queue();
                } else {
                    DiscordBot.getMapListGenerator().setTitle(value);
                    event.reply("Dieser Titel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                }
            }
            case GENERAL -> {
                if (!type[1].equals("color")) {
                    notImplemented(event);
                } else if (value == null) {
                    event.reply(String.format("Die jetzige Farbe hat den HEX-Code %s",
                            SunnySettings.GENERAL.string("color"))).setEphemeral(true).queue();
                } else {
                    try {
                        Color c = Color.decode(value);
                        SunnySettings.GENERAL.setValue("color", c);
                        event.reply("Diese Farbe wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                    } catch (NumberFormatException ex) {
                        event.reply("Das ist kein Hex-Code.").setEphemeral(true).queue();
                    }
                }
            }
            default -> notImplemented(event);
        }
    }

    /**
     * Handles the {@code <setting>:channel} settings, which all behave the same: show the currently
     * configured channel when no value is given, reject a value that is not a valid channel id, and
     * otherwise store it.
     *
     * @param currentFormat a {@link String#format} pattern with a single {@code %s} for the mention
     */
    private static void channelSetting(SlashCommandInteractionEvent event, Guild guild, SunnySettings setting,
            String[] type, String currentFormat) throws Exception {
        if (!type[1].equals("channel")) {
            notImplemented(event);
            return;
        }
        String value = event.getOption("value", null, OptionMapping::getAsString);
        if (value == null) {
            event.reply(String.format(currentFormat, mentionOf(channel(guild, setting.string("channel")))))
                    .setEphemeral(true).queue();
        } else if (channel(guild, value) == null) {
            event.reply("Dieser Wert ist keine ID eines Channels.").setEphemeral(true).queue();
        } else {
            setting.setValue("channel", value);
            event.reply("Dieser Channel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
        }
    }

    private static void notImplemented(SlashCommandInteractionEvent event) {
        event.reply("Diese Einstellung wurde noch nicht implementiert.").setEphemeral(true).queue();
    }

    /** {@code getAsMention()} for a possibly missing entity, with a readable placeholder otherwise. */
    private static String mentionOf(IMentionable entity) {
        return entity == null ? "*(nicht gesetzt)*" : entity.getAsMention();
    }

    /** Resolves a role from a stored/entered id, tolerating {@code null} and malformed ids. */
    private static Role role(Guild guild, String id) {
        try {
            return id == null ? null : guild.getRoleById(id);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    /** Resolves a channel from a stored/entered id, tolerating {@code null} and malformed ids. */
    private static GuildChannel channel(Guild guild, String id) {
        try {
            return id == null ? null : guild.getGuildChannelById(id);
        } catch (NumberFormatException ex) {
            return null;
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
