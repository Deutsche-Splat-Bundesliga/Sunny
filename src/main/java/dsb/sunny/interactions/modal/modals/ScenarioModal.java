package dsb.sunny.interactions.modal.modals;

import dsb.sunny.DiscordBot;
import dsb.sunny.interactions.modal.handler.SunnyModal;
import dsb.sunny.scenarios.SplatoonScenarios;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class ScenarioModal implements SunnyModal {
    private SplatoonScenarios scenarios;
    @Override
    public void handle(@NotNull ModalInteractionEvent event, String modalAction) throws Exception {
        if (scenarios == null) {
            scenarios = DiscordBot.getScenarios();
        }

        switch (modalAction.split(";")[0]) {
            case "create" -> onCreate(event);
            case "edit" -> onEdit(event, modalAction);
        }
    }

    private void onEdit(@NotNull ModalInteractionEvent event, String modalAction) throws Exception {
        int id = Integer.parseInt(modalAction.split(";")[1]);

        String title = event.getValue("title").getAsString();
        String rulesDe = event.getValue("rules_de").getAsString();
        String rulesEn = event.getValue("rules_en").getAsString();

        if (title.isEmpty()) {
            event.reply("Der Titel ist leer.").setEphemeral(true).queue();
        } else if (rulesDe.isEmpty()) {
            event.reply("Die Regeln sind leer.").setEphemeral(true).queue();
        } else {
            scenarios.editScenario(id, title, rulesDe, rulesEn);
            event.reply("Das Szenario wurde bearbeitet!").setEphemeral(true).queue();
        }
    }

    private void onCreate(@NotNull ModalInteractionEvent event) throws Exception {
        String title = event.getValue("title").getAsString();
        String rulesDe = event.getValue("rules_de").getAsString();
        String rulesEn = event.getValue("rules_en").getAsString();

        if (title.isEmpty()) {
            event.reply("Der Titel ist leer.").setEphemeral(true).queue();
        } else if (rulesDe.isEmpty()) {
            event.reply("Die deutschen Regeln sind leer.").setEphemeral(true).queue();
        } else if (rulesEn.isEmpty()) {
            event.reply("Die englischen Regeln sind leer.").setEphemeral(true).queue();
        } else {
            scenarios.addScenario(title, rulesDe, rulesEn);
            event.reply("Das Szenario wurde hinzugefügt!").setEphemeral(true).queue();
        }
    }
}
