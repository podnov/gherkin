package com.evanzeimet.gherkin.formatter.cli;

public class GherkinFormatterCLIMain {

    protected static void exit(boolean success) {
        Integer exitCode;

        if (success) {
            exitCode = 0;
        } else {
            exitCode = 1;
        }

        System.exit(exitCode);
    }

    public static void main(String[] args) {
        GherkinFormatterCLI cli = new GherkinFormatterCLI();

        boolean success = cli.run(args);

        exit(success);
    }
}
