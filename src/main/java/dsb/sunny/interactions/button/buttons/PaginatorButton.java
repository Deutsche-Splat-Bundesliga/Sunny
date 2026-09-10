package dsb.sunny.interactions.button.buttons;

import dsb.sunny.DiscordBot;
import dsb.sunny.interactions.button.handler.SunnyButton;
import dsb.sunny.paginator.Paginator;
import dsb.sunny.paginator.instance.PaginatorInstance;
import net.dv8tion.jda.api.components.MessageTopLevelComponentUnion;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;

import java.util.LinkedList;
import java.util.List;

public class PaginatorButton implements SunnyButton {
    @Override
    public void handle(ButtonInteractionEvent event, String action) throws Exception {
        Paginator handler = DiscordBot.getPaginator();
        Message message = event.getMessage();
        PaginatorInstance instance = handler.getPaginatorInstance(message);

        if (instance != null) {
            if (!action.equalsIgnoreCase("select")) event.deferEdit().queue();
            switch (action) {
                case "first" -> message.editMessage(instance.goToFirstPage()).queue();
                case "prev" -> message.editMessage(instance.goToPrevPage()).queue();
                case "next" -> message.editMessage(instance.goToNextPage()).queue();
                case "last" -> message.editMessage(instance.goToLastPage()).queue();

                case "select" -> {
                    TextInput page = TextInput.create("page", TextInputStyle.SHORT)
                            .setRequired(true)
                            .setMaxLength(4)
                            .build();

                    Modal modal = Modal.create("PAGINATOR:select", "Go to Page:")
                            .addComponents(Label.of("Seite", page))
                            .build();

                    event.replyModal(modal).queue();
                }
            }
        } else {
            MessageTopLevelComponentUnion topLevelComponentUnion = message.getComponents().getFirst();
            if (topLevelComponentUnion instanceof ActionRow row) {
                List<Button> buttons = new LinkedList<>();
                for (Button button : row.getButtons()) {
                    buttons.add(button.asDisabled());
                }
                message.editMessage(new MessageEditBuilder()
                                .setComponents(ActionRow.of(buttons)).build())
                        .queue();

                event.reply("Dieser Paginator ist nicht mehr aktiv.").setEphemeral(true).queue();
            }
        }
    }
}
