package dsb.sunny.command.commands.admin;

import dsb.sunny.DiscordBot;
import dsb.sunny.challonge.ChallongeModule;
import dsb.sunny.command.handler.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.modals.Modal;

public class EditScoreCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            ChallongeModule module = DiscordBot.getChallonge();
            Role divRole = event.getOption("division").getAsRole();
            if (module.getDivisions().containsKey(divRole)) {

                TextInput team1 = TextInput.create("team1", TextInputStyle.SHORT)
                        .setPlaceholder("New Squidbeak Splatoon")
                        .setRequired(true)
                        .build();

                TextInput team2 = TextInput.create("team2", TextInputStyle.SHORT)
                        .setPlaceholder("Amaterasu")
                        .setRequired(true)
                        .build();

                TextInput score = TextInput.create("score", TextInputStyle.SHORT)
                        .setPlaceholder("5-2 (Bei Forfeit: 0-ff / ff-0)")
                        .setMinLength(3)
                        .setMaxLength(4)
                        .setRequired(false)
                        .build();

                /*TextInput mvp1 = TextInput.create("mvp1", "MVP Erstes Team", TextInputStyle.SHORT)
                        .setPlaceholder("Toyoben")
                        .setRequired(false)
                        .build();

                TextInput mvp2 = TextInput.create("mvp2", "MVP Zweites Team", TextInputStyle.SHORT)
                        .setPlaceholder("Wadsm")
                        .setRequired(false)
                        .build();
                */

                Modal modal = Modal.create(String.format("challonge:%d", divRole.getIdLong()), "Korrigiere ein Ergebnis:")
                        .addComponents(
                                Label.of("Erstes Team", team1),
                                Label.of("Zweites Team", team2),
                                Label.of("Punktzahl", score))
                        .build();

                event.replyModal(modal).queue();
            } else {
                event.reply("Diese Division ist nicht eingetragen, daher kann ich keine Scores bearbeiten.").setEphemeral(true).queue();
            }
        } else {
            event.reply("Du bist kein Turnierleiter und kannst demnach diesen Command nicht nutzen.").setEphemeral(true).queue();
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("editscore", "Korrigiert ein Ergebnis in einer Division.")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
                .addOption(OptionType.ROLE, "division", "Die Divisionsrolle der betroffenen Division.", true);
    }
}
