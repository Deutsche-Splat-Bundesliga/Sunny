package dsb.sunny.interactions.modal.modals;

import dsb.sunny.DiscordBot;
import dsb.sunny.interactions.modal.handler.SunnyModal;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public class ChallongeModal implements SunnyModal {

    @Override
    public void handle(@NotNull ModalInteractionEvent event, String modalAction) throws Exception {
        event.deferReply(true).queue();
        if (modalAction.equalsIgnoreCase("report")) {
            DiscordBot.getChallonge().reportScore(event);
            return;
        }

        Role divRole = event.getGuild().getRoleById(modalAction);
        DiscordBot.getChallonge().editScore(event, divRole);
    }
}
