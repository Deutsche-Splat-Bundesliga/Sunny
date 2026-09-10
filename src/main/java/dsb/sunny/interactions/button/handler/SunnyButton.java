package dsb.sunny.interactions.button.handler;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface SunnyButton {

    void handle(ButtonInteractionEvent event, String action) throws Exception;
}
