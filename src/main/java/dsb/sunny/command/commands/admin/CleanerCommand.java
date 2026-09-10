package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.channelcleaner.ChannelCleaner;
import dsb.sunny.command.handler.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.sql.SQLException;

public class CleanerCommand implements SlashCommand {

    private ChannelCleaner cleaner;

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (cleaner == null) {
            cleaner = DiscordBot.getChannelCleaner();
        }

        if (!event.getMember().getPermissions().contains(Permission.MANAGE_CHANNEL)) {
            event.reply("Dir fehlen dafür die nötigen Permissions (Kanäle verwalten).").setEphemeral(true).queue();
            return;
        }

        if (!event.getMember().getPermissions().contains(Permission.MANAGE_ROLES)) {
            event.reply("Dir fehlen dafür die nötigen Permissions (Rollen verwalten).").setEphemeral(true).queue();
            return;
        }

        switch (event.getSubcommandName()) {
            case "message" -> handleMessage(event);
            case "channel" -> handleChannel(event);
            case "role" -> handleRole(event);
            case "clean" -> {
                event.reply("Wird gemacht! o7").setEphemeral(true).queue();
                cleaner.clearChannels();
            }
        }
    }

    private void handleRole(SlashCommandInteractionEvent event) throws SQLException {
        Role role = event.getOption("role").getAsRole();
        switch (event.getSubcommandGroup().toLowerCase()) {
            case "add" -> {
                if (cleaner.addRole(role)) {
                    event.reply("Diese Rolle wurde erfolgreich registriert!").setEphemeral(true).queue();
                } else {
                    event.reply("Diese Rolle ist bereits registriert.").setEphemeral(true).queue();
                }
            }
            case "del" -> {
                if (cleaner.removeRole(role)) {
                    event.reply("Diese Rolle wurde erfolgreich vom Cleaner entfernt!").setEphemeral(true).queue();
                } else {
                    event.reply("Diese Rolle ist bereits vom Cleaner entfernt.").setEphemeral(true).queue();
                }
            }
        }
    }

    private void handleChannel(SlashCommandInteractionEvent event) throws SQLException {
        GuildMessageChannel channel = event.getOption("channel").getAsChannel().asGuildMessageChannel();
        switch (event.getSubcommandGroup().toLowerCase()) {
            case "add" -> {
                if (cleaner.addChannel(channel)) {
                    event.reply("Dieser Channel wurde erfolgreich registriert!").setEphemeral(true).queue();
                } else {
                    event.reply("Dieser Channel ist bereits registriert.").setEphemeral(true).queue();
                }
            }
            case "del" -> {
                if (cleaner.removeChannel(channel)) {
                    event.reply("Dieser Channel wurde erfolgreich vom Cleaner entfernt!").setEphemeral(true).queue();
                } else {
                    event.reply("Dieser Channel ist bereits vom Cleaner entfernt.").setEphemeral(true).queue();
                }
            }
        }
    }

    private void handleMessage(SlashCommandInteractionEvent event) throws SQLException {
        try {
            GuildMessageChannel channel = event.getOption("channel").getAsChannel().asGuildMessageChannel();
            long id = event.getOption("message_id", OptionMapping::getAsLong);

            switch (event.getSubcommandGroup().toLowerCase()) {
                case "add" -> {
                    if (cleaner.addMessageToChannel(channel, id)) {
                        event.reply("Diese Nachricht wurde erfolgreich registriert und wird nun festgehalten!").setEphemeral(true).queue();
                    } else {
                        event.reply("Diese Nachricht wird bereits schon festgehalten.").setEphemeral(true).queue();
                    }
                }
                case "del" -> {
                    if (cleaner.removeMessageFromChannel(channel, id)) {
                        event.reply("Diese Nachricht wurde erfolgreich losgelassen!").setEphemeral(true).queue();
                    } else {
                        event.reply("Diese Nachricht wird nicht mal festgehalten.").setEphemeral(true).queue();
                    }
                }
            }
        } catch (NumberFormatException e) {
            event.reply("Das, was du mir gegeben hast, ist keine ID.").setEphemeral(true).queue();
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("cleaner", "Channel-Cleaner")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES))
                .addSubcommandGroups(new SubcommandGroupData("add", "Fügt was hinzu")
                        .addSubcommands(new SubcommandData("channel", "Fügt einen Text-Kanal hinzu.")
                                .addOption(OptionType.CHANNEL, "channel", "Der Text-Kanal", true))
                        .addSubcommands(new SubcommandData("role", "Fügt eine neue Rolle hinzu")
                                .addOption(OptionType.ROLE, "role", "Die Rolle, die zum Cleaner hinzugefügt werden soll", true))
                        .addSubcommands(new SubcommandData("message", "Behält eine Nachricht in einem Channel.")
                                .addOption(OptionType.CHANNEL, "channel", "Der Text-Kanal", true)
                                .addOption(OptionType.STRING, "message_id", "Die ID der Message", true)))

                .addSubcommandGroups(new SubcommandGroupData("del", "Entfernt etwas")
                        .addSubcommands(new SubcommandData("channel", "Fügt einen Text-Kanal hinzu.")
                                .addOption(OptionType.CHANNEL, "channel", "Der Text-Kanal", true))
                        .addSubcommands(new SubcommandData("role", "Fügt eine neue Rolle hinzu")
                                .addOption(OptionType.ROLE, "role", "Die Rolle, die zum Cleaner hinzugefügt werden soll", true))
                        .addSubcommands(new SubcommandData("message", "Behält eine Nachricht in einem Channel.")
                                .addOption(OptionType.CHANNEL, "channel", "Der Text-Kanal", true)
                                .addOption(OptionType.STRING, "message_id", "Die ID der Message", true)))

                .addSubcommandGroups(new SubcommandGroupData("do", "Tu etwas!")
                        .addSubcommands(new SubcommandData("clean", "Fängt an zu säubern."))
                );
    }
}
