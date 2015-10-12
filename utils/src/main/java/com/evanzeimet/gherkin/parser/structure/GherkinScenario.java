package com.evanzeimet.gherkin.parser.structure;

import java.util.ArrayList;
import java.util.List;


public class GherkinScenario {

    private List<GherkinLine> lines = new ArrayList<GherkinLine>();

    public GherkinScenario() {

    }

    public List<GherkinLine> getLines() {
        return lines;
    }

    public void setLines(List<GherkinLine> lines) {
        this.lines = lines;
    }

}
