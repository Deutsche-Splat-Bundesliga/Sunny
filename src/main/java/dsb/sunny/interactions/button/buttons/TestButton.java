package dsb.sunny.interactions.button.buttons;

import dsb.sunny.interactions.button.handler.SunnyButton;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class TestButton implements SunnyButton {

    @Override
    public void handle(ButtonInteractionEvent event, String action) throws Exception {
        event.replyFormat("Hello World! (Triggered by action {})", action).queue();
    }
}
