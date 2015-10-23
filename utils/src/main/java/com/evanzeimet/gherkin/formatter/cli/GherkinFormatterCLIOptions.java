package com.evanzeimet.gherkin.formatter.cli;

import java.util.List;

public class GherkinFormatterCLIOptions {

    private List<String> filenames;

    public GherkinFormatterCLIOptions() {

    }

    public List<String> getFilenames() {
        return filenames;
    }

    public void setFilenames(List<String> filenames) {
        this.filenames = filenames;
    }

}
