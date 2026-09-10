package dsb.sunny.interactions.button.buttons;

import dsb.sunny.DiscordBot;
import dsb.sunny.challonge.ChallongeModule;
import dsb.sunny.interactions.button.handler.SunnyButton;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class DropButton implements SunnyButton {
    @Override
    public void handle(ButtonInteractionEvent event, String action) throws Exception {
        ChallongeModule cm = DiscordBot.getChallonge();

        switch (action) {
            case "decline" -> event.editMessage("Vorgang abgebrochen.").setReplace(true).queue();
            case "drop" -> cm.createDropRequest(event);
            default -> {
                if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {
                    InteractionHook interaction = event.deferEdit().complete();

                    int id = Integer.parseInt(action);
                    String dropAction = switch (event.getButton().getLabel()) {
                        case "Drop ablehnen" -> "reject";
                        case "Droppen und Spiele annullieren" -> "nullify";
                        case "Droppen und restliche Spiele 0-5 werten" -> "score";
                        default ->
                            throw new IllegalArgumentException("Illegal argument: " + event.getButton().getLabel());
                    };
                    cm.dropTeam(id, dropAction, event.getUser());
                    MessageEmbed old = event.getMessage().getEmbeds().get(0);
                    MessageEmbed me = new EmbedBuilder(old)
                            .clearFields()
                            .setDescription("Drop bearbeitet von: " + event.getMember().getAsMention())
                            .addField("Aktion:", event.getButton().getLabel(), false)
                            .build();
                    interaction.editOriginalEmbeds(me)
                            .setContent(
                                    "Der Drop wurde von %s durchgeführt.".formatted(event.getMember().getAsMention()))
                            .setReplace(true)
                            .queue();
                } else {
                    event.reply("Nur Turnierleiter können diese Knöpfe drücken.").setEphemeral(true).queue();
                }
            }
        }
    }
}
