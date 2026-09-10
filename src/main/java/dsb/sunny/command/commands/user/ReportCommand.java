package dsb.sunny.command.commands.user;

import dsb.sunny.command.handler.SlashCommand;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.modals.Modal;

public class ReportCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        Role divisionRole = event.getMember().getRoles().stream().filter(role -> role.getName().contains("Division"))
                .findFirst().orElse(null);
        if (divisionRole == null) {
            event.reply("Du bist kein Kapitän eines Teams und kannst daher diesen Befehl nicht benutzen.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        TextInput opponent = TextInput.create("opponent", TextInputStyle.SHORT)
                .setPlaceholder("Team Rasensprenger")
                .setRequired(true)
                .build();

        TextInput score = TextInput.create("score", TextInputStyle.SHORT)
                .setPlaceholder("5-2 (Bei Forfeit: 0-ff / ff-0)")
                .setMaxLength(4)
                .setRequired(true)
                .build();

        Modal modal = Modal.create("challonge:report", "Melde Ergebnisse aus einem Match")
                .addComponents(
                        Label.of("Gegnerischer Teamname", opponent),
                        Label.of("Punktestand (Dein Team - Gegner)", score)
                )
                .build();

        event.replyModal(modal).queue();
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("report", "Melde Ergebnisse eines Matches");
    }
}
