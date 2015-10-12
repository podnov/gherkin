package com.evanzeimet.gherkin.parser.structure;

import java.util.ArrayList;
import java.util.List;

public class GherkinFeature {

    private GherkinFeaturePrologue prologue = new GherkinFeaturePrologue();
    private GherkinBackground background = new GherkinBackground();
    private List<GherkinScenario> scenarios = new ArrayList<GherkinScenario>();

    public GherkinFeature() {

    }

    public GherkinBackground getBackground() {
        return background;
    }

    public void setBackground(GherkinBackground background) {
        this.background = background;
    }

    public GherkinFeaturePrologue getPrologue() {
        return prologue;
    }

    public void setPrologue(GherkinFeaturePrologue prologue) {
        this.prologue = prologue;
    }

    public List<GherkinScenario> getScenarios() {
        return scenarios;
    }

    public void setScenarios(List<GherkinScenario> scenarios) {
        this.scenarios = scenarios;
    }
}
