package dsb.sunny.interactions.button.handler;

import dsb.sunny.interactions.button.buttons.*;
import dsb.sunny.interactions.module.InteractionModule;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SunnyButtonHandler extends ListenerAdapter {

    private final Logger logger = LoggerFactory.getLogger(SunnyButtonHandler.class);
    private final Map<InteractionModule, SunnyButton> sunnyButtons = new HashMap<>();

    public SunnyButtonHandler() {
        sunnyButtons.put(InteractionModule.TEST, new TestButton());
        sunnyButtons.put(InteractionModule.CAPROLE, new CapRoleButton());
        sunnyButtons.put(InteractionModule.DROP, new DropButton());
        sunnyButtons.put(InteractionModule.RW, new RWButton());
        sunnyButtons.put(InteractionModule.PAGINATOR, new PaginatorButton());
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        try {
            if (!SunnyButtonAvailabilityCheck.isAvailable(event)) return;

            String[] buttonArgs = event.getComponentId().split(":");
            InteractionModule module = InteractionModule.valueOf(buttonArgs[1].toUpperCase());
            String buttonAction = buttonArgs[2];

            SunnyButton button = sunnyButtons.get(module);
            if (button != null) {
                button.handle(event, buttonAction);
            }
        } catch (Exception e) {
            if (event.isAcknowledged()) {
                event.getHook().editOriginal("An internal error occurred, please report to the developers.").queue();
            } else {
                event.reply("An internal error occurred, please report to the developers.")
                        .setEphemeral(true)
                        .queue();
            }
            logger.error("Could not handle button interaction", e);
        }
    }
}
