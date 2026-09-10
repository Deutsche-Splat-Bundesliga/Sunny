package dsb.sunny.command.commands.user;

import dsb.sunny.command.handler.SlashCommand;
import dsb.sunny.enums.ChannelReferences;
import kotlin.text.Regex;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import java.time.OffsetDateTime;

public class DropCommand implements SlashCommand {

    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        Role divRole = event.getMember().getRoles().stream().filter(role -> role.getName().contains("Division"))
                .findFirst().orElse(null);
        if (divRole == null) {
            event.reply("Ich weiß leider nicht, für welches Team du spielst. Bitte melde dich beim "
                    + ChannelReferences.HELPDESK.getAsMention()
                    + " und bespreche es da.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        Regex teamNameRegex = new Regex("^\\[(.+)\\]");
        if (event.getMember().getNickname() == null
                || !teamNameRegex.containsMatchIn(event.getMember().getNickname())) {
            event.reply("Du bist kein Kapitän eines Teams und kannst daher diesen Befehl nicht benutzen.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Bestätigung: Drop-Antrag aus der Liga")
                .setColor(divRole.getColors().getPrimary())
                .setTitle("Wichtige Informationen vor der Entscheidung:")
                .setDescription(
                        """
                                Sobald ein Drop-Antrag gestellt wurde, kann es nicht wieder rückgängig gemacht werden.
                                Die TOs werden jedoch in der Lage sein, diesen Antrag stattzugeben oder aber auch abzulehnen.
                                Du wirst als Captain per DM über deinen Antrag benachrichtigt, sobald dieser bearbeitet wurde.
                                Während der Drop-Request bearbeitet wird, kannst du weder `/givecaprole` noch `/drop` benutzen.

                                Bist du dir sicher, dass du dein Team aus der Liga zurückziehen willst? Sobald du zustimmst, **gibt es kein Zurück mehr!**
                                """)
                .setTimestamp(OffsetDateTime.now());
        Button accept = Button
                .danger(String.format("%d:drop:drop", event.getUser().getIdLong()), "Ich ziehe mein Team zurück.")
                .asEnabled();
        Button decline = Button
                .secondary(String.format("%d:drop:decline", event.getUser().getIdLong()), "Neeee, doch lieber nicht...")
                .asEnabled();

        event.replyEmbeds(eb.build())
                .addComponents(ActionRow.of(accept, decline))
                .queue();
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("drop",
                "Stellt einen Drop-Antrag aus der Liga. Sobald einer gestellt wird, gibt es kein Zurück mehr.");
    }
}
