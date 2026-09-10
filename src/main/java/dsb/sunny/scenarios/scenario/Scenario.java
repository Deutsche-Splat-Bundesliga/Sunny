package dsb.sunny.scenarios.scenario;

import dsb.sunny.scenarios.roll.ScenarioRngRoll;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.Set;

public interface Scenario {
    private static String[] formatRules(String rulesDe, String rulesEn) {
        ScenarioRngRoll rngRoll = new ScenarioRngRoll();
        return rngRoll.formatRules(rulesDe, rulesEn);
    }

    int getId();

    String getTitle();

    String getRulesDe();

    String getRulesEn();

    Set<Scenario> getContradictingScenarios();

    void addContradictingScenario(Scenario scenario);

    void removeContradictingScenario(Scenario scenario);

    default MessageEmbed getAsEmbed(boolean roll) {
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor("Splatoon-Szenario")
                .setColor(new Color(0xF3CD36))
                .setTitle(getTitle())
                .setDescription(getRulesDe())
                .appendDescription("\n\n")
                .appendDescription(getRulesEn())
                .setFooter("Szenario-ID #%03d".formatted(getId()));

        if (roll) {
            String[] rules = formatRules(getRulesDe(), getRulesEn());
            eb.setDescription(rules[0])
                    .appendDescription("\n\n")
                    .appendDescription(rules[1]);
        }

        return eb.build();
    }
}
