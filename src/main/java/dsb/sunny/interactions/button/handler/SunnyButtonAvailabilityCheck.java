package dsb.sunny.interactions.button.handler;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public class SunnyButtonAvailabilityCheck {

    public static boolean isAvailable(ButtonInteractionEvent event) {

        String[] id = event.getComponentId().split(":");
        String authorId = id[0];

        if (event.isAcknowledged()) {
            return false;
        }

        if (authorId.startsWith("&")) {
            Role role = event.getGuild().getRoleById(authorId.replace("&", ""));
            if (role == null || !event.getMember().getRoles().contains(role)) {
                return false;
            }
            return true;
        }

        if (isUserSnowflake(authorId)) {
            if (!(authorId.equals(event.getUser().getId()))) {
                event.deferEdit().queue();
                return false;
            }
        }
        return true;
    }

    public static boolean isUserSnowflake(String obj) {
        try {
            Long.parseLong(obj);
            return true;
        } catch(NumberFormatException ex) {
            return false;
        }
    }

}
