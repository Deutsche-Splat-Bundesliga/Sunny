package dsb.sunny.interactions.modal.handler;

import dsb.sunny.interactions.modal.modals.ChallongeModal;
import dsb.sunny.interactions.modal.modals.MappoolModal;
import dsb.sunny.interactions.modal.modals.PaginatorModal;
import dsb.sunny.interactions.modal.modals.ScenarioModal;
import dsb.sunny.interactions.module.InteractionModule;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SunnyModalHandler extends ListenerAdapter {

    private Map<InteractionModule, SunnyModal> sunnyModals = new HashMap<>();

    public SunnyModalHandler() {
        sunnyModals.put(InteractionModule.CHALLONGE, new ChallongeModal());
        sunnyModals.put(InteractionModule.MAPPOOL, new MappoolModal());
        sunnyModals.put(InteractionModule.PAGINATOR, new PaginatorModal());
        sunnyModals.put(InteractionModule.SCENARIO, new ScenarioModal());
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        try {
            String[] modalArgs = event.getModalId().split(":");
            InteractionModule module = InteractionModule.valueOf(modalArgs[0].toUpperCase());
            String modalAction = modalArgs[1];

            SunnyModal button = sunnyModals.get(module);
            if (button != null) {
                button.handle(event, modalAction);
            }
        } catch (Exception e) {
            // TODO: 07.12.2022 LOG and Exception Logger!
        }
    }
}
