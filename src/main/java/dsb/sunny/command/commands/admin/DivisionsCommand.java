package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.challonge.ChallongeModule;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class DivisionsCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            ChallongeModule srm = DiscordBot.getChallonge();
            Guild guild = event.getGuild();
            String subcommand = event.getSubcommandName();

            switch (subcommand) {
                case "set" -> {
                    Role r = event.getOption("rolle").getAsRole();
                    String url = event.getOption("challonge_id").getAsString();
                    srm.addDivision(r, url);
                    event.reply("Die Division wurde erfolgreich hinzugefügt!").setEphemeral(true).queue();
                }
                case "del" -> {
                    Role r = event.getOption("rolle").getAsRole();
                    srm.removeDivision(r);
                    event.reply("Die Division wurde erfolgreich entfernt!").setEphemeral(true).queue();
                }
                case "clear" -> {
                    srm.clearDivisions();
                    event.reply("Alle Divisionen wurden erfolgreich entfernt!").setEphemeral(true).queue();
                }
                default -> event.reply("?").setEphemeral(true).queue();
            }
        } else {
            event.reply("Du bist kein Turnierleiter und kannst daher diesen Befehl nicht benutzen.").setEphemeral(true).queue();
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("divisions", "Verwalte die Divisionen")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addSubcommands(new SubcommandData("set", "Stelle eine Division ein.")
                        .addOption(OptionType.ROLE, "rolle", "Die Rolle der Division", true)
                        .addOption(OptionType.STRING, "challonge_id", "Die Challonge-URL der Division", true))
                .addSubcommands(new SubcommandData("del", "Lösche eine Division.")
                        .addOption(OptionType.ROLE, "rolle", "Die Rolle der Division", true))
                .addSubcommands(new SubcommandData("clear", "Lösche alle Divisionen."));
    }
}
