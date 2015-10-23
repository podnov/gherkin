package com.evanzeimet.gherkin.formatter.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.evanzeimet.gherkin.formatter.GherkinFileFormatter;
import com.evanzeimet.gherkin.formatter.GherkinFormatterException;

public class GherkinFormatterCLI {

    private static final Logger logger = LoggerFactory.getLogger(GherkinFormatterCLI.class);

    public boolean run(String[] args) {
        String className = getClass().getSimpleName();

        String message = String.format("Running %s", className);
        logger.info(message);

        Boolean result;
        GherkinFormatterCLIOptions options = parseOptions(args);

        if (options == null) {
            result = false;
        } else {
            result = runFormatter(options);
        }

        message = String.format("Exiting %s with success flag [%s]", className, result);
        logger.info(message);

        return result;
    }

    protected GherkinFormatterCLIOptions parseOptions(String[] args) {
        GherkinFormatterCLIOptions result = new GherkinFormatterCLIOptions();

        List<String> filenames = new ArrayList<String>();

        if (args == null) {
            String message = String.format("No arguments file arguments given");
            logger.info(message);
        } else {
            filenames.addAll(Arrays.asList(args));
        }

        result.setFilenames(filenames);

        return result;
    }

    protected Boolean runFormatter(GherkinFormatterCLIOptions options) {
        Boolean result;

        List<String> filenames = options.getFilenames();
        GherkinFileFormatter formatter = new GherkinFileFormatter();

        try {
            formatter.formatFilenames(filenames);
            result = true;
        } catch (GherkinFormatterException e) {
            result = false;

            String message = "Could not format files";
            logger.error(message, e);
        }

        return result;
    }
}
