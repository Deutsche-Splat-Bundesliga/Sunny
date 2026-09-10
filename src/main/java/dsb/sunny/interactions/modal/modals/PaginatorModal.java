package dsb.sunny.interactions.modal.modals;

import dsb.sunny.DiscordBot;
import dsb.sunny.interactions.modal.handler.SunnyModal;
import dsb.sunny.paginator.Paginator;
import dsb.sunny.paginator.instance.PaginatorInstance;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class PaginatorModal implements SunnyModal {
    @Override
    public void handle(@NotNull ModalInteractionEvent event, String action) throws Exception {
        Paginator handler = DiscordBot.getPaginator();
        Message message = event.getMessage();

        try {
            int index = Integer.parseInt(event.getValue("page").getAsString()) - 1;
            if (message != null) {
                PaginatorInstance instance = handler.getPaginatorInstance(message);
                if (instance != null) {
                    message.editMessage(instance.turnToPage(index)).queue();
                }
                event.deferEdit().queue();
            } else {
                event.reply("Der Paginator ist schon abgelaufen.").setEphemeral(true).queue();
            }
        } catch (IndexOutOfBoundsException ex) {
            event.reply("Ich konnte diese Seite nicht finden.").setEphemeral(true).queue();
        } catch (NumberFormatException ex) {
            event.reply("Das ist keine gültige Zahl.").setEphemeral(true).queue();
        }
    }
}
