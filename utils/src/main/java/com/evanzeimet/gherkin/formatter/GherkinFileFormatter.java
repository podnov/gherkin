package com.evanzeimet.gherkin.formatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.evanzeimet.gherkin.parser.GherkinParser;
import com.evanzeimet.gherkin.parser.GherkinParserException;
import com.evanzeimet.gherkin.parser.structure.GherkinBackground;
import com.evanzeimet.gherkin.parser.structure.GherkinFeature;
import com.evanzeimet.gherkin.parser.structure.GherkinFeaturePrologue;
import com.evanzeimet.gherkin.parser.structure.GherkinLine;
import com.evanzeimet.gherkin.parser.structure.GherkinScenario;

public class GherkinFileFormatter {

    protected static final String AUTO_LINE_SEPARATOR = String.format("%n");
    protected static final String CRLF = "\r\n";
    protected static final String LF = "\n";

    private GherkinFormatterLineSeparator lineSeparator = GherkinFormatterLineSeparator.AUTO;

    public GherkinFileFormatter() {

    }

    public GherkinFormatterLineSeparator getLineSeparator() {
        return lineSeparator;
    }

    public void setLineSeparator(GherkinFormatterLineSeparator lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    protected String chooseLineSeparator() {
        String result = AUTO_LINE_SEPARATOR;

        if (lineSeparator == null) {
            lineSeparator = GherkinFormatterLineSeparator.AUTO;
        }

        switch (lineSeparator) {
            case AUTO:
                result = AUTO_LINE_SEPARATOR;
                break;

            case UNIX:
                result = LF;
                break;

            case WINDOWS:
                result = CRLF;
                break;
        }

        return result;
    }

    protected String createFeatureFileContents(GherkinFeature feature) {
        List<String> lines = getFeatureLines(feature);
        String lineSeparator = chooseLineSeparator();
        return StringUtils.join(lines, lineSeparator);
    }

    protected boolean diffOutputTarget(String newContents, File outputFile) throws GherkinFormatterException {
        String currentContents = readFile(outputFile);
        return !newContents.equals(currentContents);
    }

    public void formatFile(File input) throws GherkinFormatterException {
        formatFile(input, input);
    }

    public void formatFile(File inputFile, File outputFile) throws GherkinFormatterException {
        String inputFileContents = readFile(inputFile);

        GherkinParser parser = new GherkinParser();
        GherkinFeature feature;

        try {
            feature = parser.parse(inputFileContents);
        } catch (GherkinParserException e) {
            String message = "Could not parse contents";
            throw new GherkinFormatterException(message, e);
        }

        GherkinFormatter formatter = new GherkinFormatter();
        formatter.format(feature);

        writeFeature(feature, outputFile);
    }

    public void formatFilename(String input) throws GherkinFormatterException {
        formatFilename(input, input);
    }

    public void formatFilename(String input, String output) throws GherkinFormatterException {
        File inputFile = new File(input);
        File outputFile = new File(output);
        formatFile(inputFile, outputFile);
    }

    public void formatFiles(List<File> files) throws GherkinFormatterException {
        if (files != null) {
            for (File file : files) {
                formatFile(file);
            }
        }
    }

    public void formatFilenames(List<String> filenames) throws GherkinFormatterException {
        if (filenames != null) {
            for (String filename : filenames) {
                formatFilename(filename);
            }
        }
    }

    protected List<String> getFeatureLines(GherkinFeature feature) {
        List<String> result = new ArrayList<String>();

        GherkinFeaturePrologue prologue = feature.getPrologue();
        List<GherkinLine> gherkinLines = prologue.getLines();

        List<String> lineTexts = getLineTexts(gherkinLines);
        result.addAll(lineTexts);

        GherkinBackground background = feature.getBackground();
        gherkinLines = background.getLines();

        lineTexts = getLineTexts(gherkinLines);
        result.addAll(lineTexts);

        List<GherkinScenario> scenarios = feature.getScenarios();

        for (GherkinScenario scenario : scenarios) {
            gherkinLines = scenario.getLines();
            lineTexts = getLineTexts(gherkinLines);
            result.addAll(lineTexts);
        }

        return result;
    }

    protected List<String> getLineTexts(List<GherkinLine> gherkinLines) {
        int lineCount = gherkinLines.size();
        List<String> result = new ArrayList<String>(lineCount);

        for (GherkinLine gherkinLine : gherkinLines) {
            String text = gherkinLine.getText();
            result.add(text);
        }

        return result;
    }

    protected String readFile(File inputFile) throws GherkinFormatterException {
        String originalFileContents = null;

        try {
            originalFileContents = FileUtils.readFileToString(inputFile);
        } catch (IOException e) {
            String message = String.format("Could not read [%s]", inputFile.getAbsolutePath());
            throw new GherkinFormatterException(message);
        }

        return originalFileContents;
    }

    protected void writeFeature(GherkinFeature feature, File outputFile) throws GherkinFormatterException {
        String fileContents = createFeatureFileContents(feature);

        boolean contentsChanged = diffOutputTarget(fileContents, outputFile);

        if (contentsChanged) {
            try {
                FileUtils.write(outputFile, fileContents);
            } catch (IOException e) {
                String filePath = outputFile.getAbsolutePath();
                String message = String.format("Could not write feature to [%s]", filePath);
                throw new GherkinFormatterException(message, e);
            }
        }
    }
}
