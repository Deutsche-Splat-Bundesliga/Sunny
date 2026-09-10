package dsb.sunny.interactions.modal.handler;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface SunnyModal {
    void handle(@NotNull ModalInteractionEvent event, String modalAction) throws Exception;
}
