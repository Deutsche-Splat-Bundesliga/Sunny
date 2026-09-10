package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.nowrite.NoWriteRoleModule;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.EnumSet;

public class NoWriteRoleCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        EnumSet<Permission> enumSet = event.getMember().getPermissions();
        if (enumSet.contains(Permission.MANAGE_ROLES) && enumSet.contains(Permission.MANAGE_CHANNEL)) {
            NoWriteRoleModule nwrm = DiscordBot.getNwrm();

            String subcommand = event.getSubcommandName();

            switch (subcommand) {
                case "set" -> {
                    TextChannel textChannel = event.getOption("channel").getAsChannel().asTextChannel();
                    Role role = event.getOption("rolle").getAsRole();
                    nwrm.setChannel(textChannel, role);
                    event.reply("Der Channel wurde erfolgreich eingestellt!").setEphemeral(true).queue();
                }
                case "del" -> {
                    event.reply("Die Referenzen zum Channel werden entfernt. Durch die Ratelimits kann es eine Weile dauern, bis alle Rollen entfernt werden.").setEphemeral(true).queue();
                    TextChannel textChannel = event.getOption("channel").getAsChannel().asTextChannel();
                    nwrm.deleteChannel(textChannel);
                }
                case "clear" -> {
                    event.reply("Die Referenzen zu allen Channels werden entfernt. Durch die Ratelimits kann es eine Weile dauern, bis alle Rollen entfernt werden.").setEphemeral(true).queue();
                    nwrm.clearChannels();
                }
            }
        } else {
            event.reply("Dir fehlen die Berechtigungen \"Rollen verwalten\" und \"Kanäle verwalten\", und kannst demnach diesen Befehl nicht ausführen.").setEphemeral(true).queue();
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("nowrite", "Verwalte No-Write-Roles")
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MANAGE_ROLES, Permission.MANAGE_CHANNEL))
                .addSubcommands(new SubcommandData("set", "Stelle eine Zuordnung eines NoWriteRole für ein Channel ein.")
                        .addOption(OptionType.CHANNEL, "channel", "Der Channel, auf das das No-Write-Role zugehören soll", true)
                        .addOption(OptionType.ROLE, "rolle", "Die No-Write-Rolle des Channels", true))
                .addSubcommands(new SubcommandData("del", "Lösche eine Zugehörigkeit des No-Write-Channels. DAS WIRD ALLE MEMBER VON DER ROLLE BEFREIEN!")
                        .addOption(OptionType.CHANNEL, "channel", "Der Channel, auf welches das No-Write-Role zugehört", true))
                .addSubcommands(new SubcommandData("clear", "Löscht alle Zugehörigkeiten. DAS WIRD ALLE MEMBER VON DER ROLLE BEFREIEN!"));
    }
}
