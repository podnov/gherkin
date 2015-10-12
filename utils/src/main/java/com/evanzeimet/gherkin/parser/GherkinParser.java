package com.evanzeimet.gherkin.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.evanzeimet.gherkin.parser.structure.GherkinBackground;
import com.evanzeimet.gherkin.parser.structure.GherkinFeature;
import com.evanzeimet.gherkin.parser.structure.GherkinFeaturePrologue;
import com.evanzeimet.gherkin.parser.structure.GherkinFeatureSection;
import com.evanzeimet.gherkin.parser.structure.GherkinLine;
import com.evanzeimet.gherkin.parser.structure.GherkinLineType;
import com.evanzeimet.gherkin.parser.structure.GherkinScenario;

public class GherkinParser {

    protected static final Pattern isAndLinePattern = Pattern.compile("^\\s*And .*");
    protected static final Pattern isBackgroundHeaderLinePattern = Pattern.compile("^\\s*Background:.*");
    protected static final Pattern isBlankLinePattern = Pattern.compile("^\\s*$");
    protected static final Pattern isCommentLinePattern = Pattern.compile("^\\s*#.*");
    protected static final Pattern isDataTableLinePattern = Pattern.compile("^\\s*\\|.*");
    protected static final Pattern isFeatureHeaderLinePattern = Pattern.compile("^\\s*Feature:.*");
    protected static final Pattern isGivenLinePattern = Pattern.compile("^\\s*Given .*");
    protected static final Pattern isScenarioHeaderLinePattern = Pattern.compile("^\\s*Scenario:.*");
    protected static final Pattern isThenLinePattern = Pattern.compile("^\\s*Then .*");
    protected static final Pattern isWhenLinePattern = Pattern.compile("^\\s*When .*");

    protected void addBackgroundLine(GherkinFeature feature, GherkinLine gherkinLine) {
        GherkinBackground background = feature.getBackground();
        background.getLines().add(gherkinLine);
    }

    protected void addNewScenario(GherkinFeature feature, GherkinLine gherkinLine) {
        List<GherkinScenario> scenarios = feature.getScenarios();
        GherkinScenario currentScenario = new GherkinScenario();
        scenarios.add(currentScenario);
        addScenarioLine(feature, gherkinLine);
    }

    protected void addPrologueLine(GherkinFeature feature, GherkinLine gherkinLine) {
        GherkinFeaturePrologue prologue = feature.getPrologue();
        prologue.getLines().add(gherkinLine);
    }

    protected void addScenarioLine(GherkinFeature feature, GherkinLine gherkinLine) {
        List<GherkinScenario> scenarios = feature.getScenarios();

        int scenarioCount = scenarios.size();
        int currentScenarioIndex = (scenarioCount - 1);

        GherkinScenario currentScenario = scenarios.get(currentScenarioIndex);
        currentScenario.getLines().add(gherkinLine);
    }

    protected String coalesceString(String original) {
        return (original == null ? "" : original);
    }

    protected void determineEffectiveLineTypes(List<GherkinLine> gherkinLines) {
        if (gherkinLines != null) {
            int lineCount = gherkinLines.size();

            if (lineCount > 0) {
                GherkinLineType previousEffectiveLineType = null;
                GherkinLineType previousGivenWhenThenLineType = null;

                for (GherkinLine gherkinLine : gherkinLines) {
                    GherkinLineType currentLineType = gherkinLine.getLineType();
                    GherkinLineType effectiveLineType = currentLineType;

                    boolean previousIsDataTable = GherkinLineType.isDataTable(previousEffectiveLineType);
                    boolean previousIsFeaturePrologue = GherkinLineType.isFeaturePrologue(previousEffectiveLineType);
                    boolean previousIsGivenWhenThen = GherkinLineType.isGivenWhenThen(previousEffectiveLineType);

                    switch (currentLineType) {
                        case AND:
                            if (previousGivenWhenThenLineType == null) {
                                // Weird to have an and with no previous given/when/then
                                effectiveLineType = currentLineType;
                            } else {
                                effectiveLineType = previousGivenWhenThenLineType;
                            }
                            break;

                        case BLANK:
                            if (previousIsGivenWhenThen) {
                                effectiveLineType = previousEffectiveLineType;
                            } else if (previousIsFeaturePrologue) {
                                effectiveLineType = GherkinLineType.FEATURE_DESCRIPTION;
                            } else {
                                effectiveLineType = GherkinLineType.BLANK;
                            }
                            break;

                        case COMMENT:
                            boolean commentRetainsPreviousEffectiveLineType = (previousIsGivenWhenThen || previousIsDataTable);

                            if (commentRetainsPreviousEffectiveLineType) {
                                effectiveLineType = previousEffectiveLineType;
                            } else if (previousIsFeaturePrologue) {
                                effectiveLineType = GherkinLineType.FEATURE_DESCRIPTION;
                            } else {
                                effectiveLineType = GherkinLineType.COMMENT;
                            }
                            break;

                        case DATA_TABLE:
                            if (GherkinLineType.isDataTable(previousEffectiveLineType)) {
                                effectiveLineType = GherkinLineType.DATA_TABLE_BODY;
                            } else {
                                effectiveLineType = GherkinLineType.DATA_TABLE_HEADER;
                            }
                            break;

                        case BACKGROUND_HEADER:
                        case FEATURE_HEADER:
                        case GIVEN:
                        case SCENARIO_HEADER:
                        case THEN:
                        case WHEN:
                            effectiveLineType = currentLineType;
                            break;

                        case OTHER:
                            if (GherkinLineType.isFeaturePrologue(previousEffectiveLineType)) {
                                effectiveLineType = GherkinLineType.FEATURE_DESCRIPTION;
                            } else {
                                effectiveLineType = GherkinLineType.OTHER;
                            }
                            break;

                        case DATA_TABLE_BODY:
                        case DATA_TABLE_HEADER:
                        case FEATURE_DESCRIPTION:
                            // these should never be actual line types, only effective
                            break;
                    }

                    gherkinLine.setEffectiveLineType(effectiveLineType);
                    previousEffectiveLineType = effectiveLineType;

                    boolean isNotDataTable = !GherkinLineType.isDataTable(effectiveLineType);
                    boolean currentLineAffectsGivenWhenThen = (isNotDataTable);

                    if (currentLineAffectsGivenWhenThen) {
                        if (GherkinLineType.isGivenWhenThen(effectiveLineType)) {
                            previousGivenWhenThenLineType = effectiveLineType;
                        } else {
                            previousGivenWhenThenLineType = null;
                        }
                    }
                }
            }
        }
    }

    protected GherkinLineType determineGherkinLineType(String stringLine) {
        GherkinLineType result;

        if (isAndLine(stringLine)) {
            result = GherkinLineType.AND;
        } else if (isBackgroundHeaderLine(stringLine)) {
            result = GherkinLineType.BACKGROUND_HEADER;
        } else if (isBlankLine(stringLine)) {
            result = GherkinLineType.BLANK;
        } else if (isCommentLine(stringLine)) {
            result = GherkinLineType.COMMENT;
        } else if (isDataTableLine(stringLine)) {
            result = GherkinLineType.DATA_TABLE;
        } else if (isFeatureHeaderLine(stringLine)) {
            result = GherkinLineType.FEATURE_HEADER;
        } else if (isGivenLine(stringLine)) {
            result = GherkinLineType.GIVEN;
        } else if (isScenarioHeaderLine(stringLine)) {
            result = GherkinLineType.SCENARIO_HEADER;
        } else if (isThenLine(stringLine)) {
            result = GherkinLineType.THEN;
        } else if (isWhenLine(stringLine)) {
            result = GherkinLineType.WHEN;
        } else {
            result = GherkinLineType.OTHER;
        }

        return result;
    }

    protected GherkinLineType determineNextDataTableLineType(GherkinLine previousNonCommentGherkinLine) {
        GherkinLineType result;

        if (previousNonCommentGherkinLine == null) {
            result = GherkinLineType.DATA_TABLE_HEADER;
        } else {
            GherkinLineType previousLineType = previousNonCommentGherkinLine.getLineType();

            boolean previousLineIsDataTableLine = GherkinLineType.DATA_TABLE_BODY.equals(previousLineType);
            previousLineIsDataTableLine = (previousLineIsDataTableLine || GherkinLineType.DATA_TABLE_HEADER.equals(previousLineType));

            if (previousLineIsDataTableLine) {
                result = GherkinLineType.DATA_TABLE_BODY;
            } else {
                result = GherkinLineType.DATA_TABLE_HEADER;
            }
        }

        return result;
    }

    protected Boolean isAndLine(String line) {
        return isAndLinePattern.matcher(line).matches();
    }

    protected Boolean isBackgroundHeaderLine(String line) {
        return isBackgroundHeaderLinePattern.matcher(line).matches();
    }

    protected Boolean isBlankLine(String line) {
        return isBlankLinePattern.matcher(line).matches();
    }

    protected Boolean isCommentLine(String line) {
        return isCommentLinePattern.matcher(line).matches();
    }

    protected Boolean isDataTableLine(String line) {
        return isDataTableLinePattern.matcher(line).matches();
    }


    protected Boolean isFeatureHeaderLine(String line) {
        return isFeatureHeaderLinePattern.matcher(line).matches();
    }

    protected Boolean isGivenLine(String line) {
        return isGivenLinePattern.matcher(line).matches();
    }

    protected Boolean isScenarioHeaderLine(String line) {
        return isScenarioHeaderLinePattern.matcher(line).matches();
    }

    protected Boolean isThenLine(String line) {
        return isThenLinePattern.matcher(line).matches();
    }

    protected Boolean isWhenLine(String line) {
        return isWhenLinePattern.matcher(line).matches();
    }

    public GherkinFeature parse(String original) throws GherkinParserException {
        original = coalesceString(original);

        List<String> stringLines = splitStringLines(original);
        List<GherkinLine> gherkinLines = parseStringLines(stringLines);
        determineEffectiveLineTypes(gherkinLines);

        return parseGherkinLines(gherkinLines);
    }

    protected GherkinFeatureSection parseGherkinLineForBackground(GherkinFeature feature,
            GherkinLine gherkinLine,
            GherkinFeatureSection currentSection) throws GherkinParserException {
        GherkinFeatureSection result = currentSection;
        GherkinLineType effectiveLineType = gherkinLine.getEffectiveLineType();

        switch (effectiveLineType) {
            case AND:
            case BLANK:
            case COMMENT:
            case DATA_TABLE:
            case DATA_TABLE_BODY:
            case DATA_TABLE_HEADER:
            case GIVEN:
            case OTHER:
            case THEN:
            case WHEN:
                // valid background lines
                addBackgroundLine(feature, gherkinLine);
                break;

            case SCENARIO_HEADER:
                result = progressFeatureSectionToScenarios(feature, gherkinLine);
                break;

            case BACKGROUND_HEADER:
            case FEATURE_HEADER:
            case FEATURE_DESCRIPTION:
                throwUnexpectedLineTypeException(gherkinLine, currentSection, effectiveLineType);
                break;
        }

        return result;
    }

    protected GherkinFeatureSection parseGherkinLineForCurrentSection(GherkinFeature feature,
            GherkinLine gherkinLine,
            GherkinFeatureSection currentSection) throws GherkinParserException {
        GherkinFeatureSection result = currentSection;

        switch (currentSection) {
            case PROLOGUE:
                result = parseGherkinLineForPrologue(feature, gherkinLine, currentSection);
                break;

            case BACKGROUND:
                result = parseGherkinLineForBackground(feature, gherkinLine, currentSection);
                break;

            case SCENARIOS:
                result = parseGherkinLineforScenarios(feature, gherkinLine, currentSection);
                break;

        }

        return result;
    }

    protected GherkinFeatureSection parseGherkinLineForPrologue(GherkinFeature feature,
            GherkinLine gherkinLine,
            GherkinFeatureSection currentSection) throws GherkinParserException {
        GherkinFeatureSection result = currentSection;
        GherkinLineType effectiveLineType = gherkinLine.getEffectiveLineType();

        switch (effectiveLineType) {
            case AND:
            case BLANK:
            case COMMENT:
            case FEATURE_HEADER:
            case FEATURE_DESCRIPTION:
            case OTHER:
                // valid prologue lines
                addPrologueLine(feature, gherkinLine);
                break;

            case BACKGROUND_HEADER:
                result = progressFeatureSectionToBackground(feature, gherkinLine);
                break;

            case SCENARIO_HEADER:
                result = progressFeatureSectionToScenarios(feature, gherkinLine);
                break;

            case DATA_TABLE:
            case DATA_TABLE_BODY:
            case DATA_TABLE_HEADER:
            case GIVEN:
            case THEN:
            case WHEN:
                throwUnexpectedLineTypeException(gherkinLine, currentSection, effectiveLineType);
        }

        return result;
    }

    protected GherkinFeatureSection parseGherkinLineforScenarios(GherkinFeature feature,
            GherkinLine gherkinLine,
            GherkinFeatureSection currentSection) throws GherkinParserException {
        GherkinFeatureSection result = currentSection;
        GherkinLineType effectiveLineType = gherkinLine.getEffectiveLineType();

        switch (effectiveLineType) {
            case AND:
            case BLANK:
            case COMMENT:
            case DATA_TABLE:
            case DATA_TABLE_BODY:
            case DATA_TABLE_HEADER:
            case GIVEN:
            case OTHER:
            case THEN:
            case WHEN:
                // valid scenario lines
                addScenarioLine(feature, gherkinLine);
                break;

            case SCENARIO_HEADER:
                addNewScenario(feature, gherkinLine);
                break;

            case BACKGROUND_HEADER:
            case FEATURE_HEADER:
            case FEATURE_DESCRIPTION:
                throwUnexpectedLineTypeException(gherkinLine, currentSection, effectiveLineType);
        }

        return result;
    }

    protected GherkinFeature parseGherkinLines(List<GherkinLine> gherkinLines) throws GherkinParserException {
        GherkinFeature result = new GherkinFeature();

        if (gherkinLines != null) {
            GherkinFeatureSection currentSection = GherkinFeatureSection.PROLOGUE;

            for (GherkinLine gherkinLine : gherkinLines) {
                currentSection = parseGherkinLineForCurrentSection(result,
                        gherkinLine,
                        currentSection);
            }
        }

        return result;
    }

    protected List<GherkinLine> parseStringLines(List<String> stringLines) {
        int lineCount = stringLines.size();
        List<GherkinLine> result = new ArrayList<GherkinLine>(lineCount);

        if (stringLines != null) {
            int lineNumber = 1;

            for (String stringLine : stringLines) {
                GherkinLine gherkinLine = processStringLine(stringLine, lineNumber);
                result.add(gherkinLine);
                lineNumber++;
            }
        }

        return result;
    }

    protected GherkinLine processStringLine(String stringLine, int lineNumber) {
        GherkinLine result = new GherkinLine();

        result.setLineNumber(lineNumber);

        GherkinLineType lineType = determineGherkinLineType(stringLine);
        result.setLineType(lineType);

        result.setText(stringLine);

        return result;
    }

    protected GherkinFeatureSection progressFeatureSectionToBackground(GherkinFeature feature,
            GherkinLine gherkinLine) {
        addBackgroundLine(feature, gherkinLine);
        return GherkinFeatureSection.BACKGROUND;
    }

    protected GherkinFeatureSection progressFeatureSectionToScenarios(GherkinFeature feature,
            GherkinLine gherkinLine) {
        List<GherkinScenario> scenarios = feature.getScenarios();

        GherkinScenario scenario = new GherkinScenario();
        scenarios.add(scenario);

        scenario.getLines().add(gherkinLine);

        return GherkinFeatureSection.SCENARIOS;
    }

    protected List<String> splitStringLines(String original) {
        List<String> result;

        if (StringUtils.isBlank(original)) {
            result = new ArrayList<String>();
        } else {
            String[] lineArray = original.split("\\r?\\n");
            List<String> immutableLineList = Arrays.asList(lineArray);
            result = new ArrayList<String>(immutableLineList);
        }

        return result;
    }

    protected void throwUnexpectedLineTypeException(GherkinLine gherkinLine,
            GherkinFeatureSection currentSection,
            GherkinLineType effectiveLineType) throws GherkinParserException {
        String lineContent = gherkinLine.getText();
        int lineNumber = gherkinLine.getLineNumber();
        String message = String.format("Found unexpected line type [%s] in section [%s] at line number [%s] with content [%s]",
                effectiveLineType,
                currentSection,
                lineNumber,
                lineContent);
        throw new GherkinParserException(message);
    }
}
