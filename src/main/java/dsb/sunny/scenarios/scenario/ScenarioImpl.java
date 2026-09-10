package dsb.sunny.scenarios.scenario;

import java.util.*;

public class ScenarioImpl implements Scenario {
    private final int id;
    private final String title;
    private final String rulesDe;
    private final String rulesEn;
    private final Set<Scenario> contradicting;

    public ScenarioImpl(int id, String title, String rulesDe, String rulesEn) {
        this.id = id;
        this.title = title;
        this.rulesDe = rulesDe;
        this.rulesEn = rulesEn;
        this.contradicting = new HashSet<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getRulesDe() {
        return rulesDe;
    }

    @Override
    public String getRulesEn() {
        return rulesEn;
    }

    @Override
    public Set<Scenario> getContradictingScenarios() {
        return Set.copyOf(contradicting);
    }

    @Override
    public void addContradictingScenario(Scenario scenario) {
        contradicting.add(scenario);
    }

    @Override
    public void removeContradictingScenario(Scenario scenario) {
        contradicting.remove(scenario);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        ScenarioImpl other = (ScenarioImpl) obj;
        return id == other.id;
    }
}
