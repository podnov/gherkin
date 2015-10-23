package com.evanzeimet.gherkin.parser.structure;

import java.util.ArrayList;
import java.util.List;


public class GherkinBackground {

    private List<GherkinLine> lines = new ArrayList<GherkinLine>();

    public GherkinBackground() {

    }

    public List<GherkinLine> getLines() {
        return lines;
    }

    public void setLines(List<GherkinLine> lines) {
        this.lines = lines;
    }
}
