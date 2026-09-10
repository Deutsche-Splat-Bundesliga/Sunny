package dsb.sunny.command.commands.admin;

import dsb.sunny.command.handler.SlashCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.modals.Modal;


public class SetMapPoolCommand implements SlashCommand {
    @Override
    public void handle(SlashCommandInteractionEvent event) throws Exception {
        if (!event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
            event.reply("Nur die Turnierleitung kann diesen Command ausführen.").setEphemeral(true).queue();
        } else {
            TextInput title = TextInput.create("title", TextInputStyle.SHORT)
                    .setPlaceholder("Season 90 Episode 471")
                    .build();
            TextInput zones = TextInput.create("zones", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Pro Zeile eine Map.")
                    .build();
            TextInput tower = TextInput.create("tower", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Pro Zeile eine Map.")
                    .build();
            TextInput rain = TextInput.create("rain", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Pro Zeile eine Map.")
                    .build();
            TextInput clam = TextInput.create("clam", TextInputStyle.PARAGRAPH)
                    .setPlaceholder("Pro Zeile eine Map.")
                    .build();

            Modal modal = Modal.create("mappool:1", "Erstelle ein Mappool")
                    .addComponents(
                            Label.of("Titel", title),
                            Label.of("Herrschaft / Splat Zones", zones),
                            Label.of("Turmkommando / Tower Control", tower),
                            Label.of("Operation Goldfisch / Rainmaker", rain),
                            Label.of("Muschelchaos / Clam Blitz", clam)
                    )
                    .build();

            event.replyModal(modal).queue();
        }
    }

    @Override
    public CommandData commandData() {
        return Commands.slash("setmappool", "Erstelle eine neue Mappool für die DSB.")
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }
}
