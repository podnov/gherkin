package com.evanzeimet.gherkin.parser.structure;

public enum GherkinLineType {
    AND,
    BACKGROUND_HEADER,
    COMMENT,
    DATA_TABLE,
    DATA_TABLE_BODY,
    DATA_TABLE_HEADER,
    BLANK,
    FEATURE_DESCRIPTION,
    FEATURE_HEADER,
    GIVEN,
    OTHER,
    SCENARIO_HEADER,
    THEN,
    WHEN;

    public static boolean isDataTable(GherkinLineType lineType) {
        boolean result = GherkinLineType.DATA_TABLE.equals(lineType);
        result = (result || GherkinLineType.DATA_TABLE_BODY.equals(lineType));
        result = (result || GherkinLineType.DATA_TABLE_HEADER.equals(lineType));
        return result;
    }

    public static boolean isFeaturePrologue(GherkinLineType lineType) {
        boolean result = GherkinLineType.FEATURE_HEADER.equals(lineType);
        result = (result || GherkinLineType.FEATURE_DESCRIPTION.equals(lineType));
        return result;
    }

    public static boolean isGivenWhenThen(GherkinLineType lineType) {
        boolean result = GherkinLineType.GIVEN.equals(lineType);
        result = (result || GherkinLineType.WHEN.equals(lineType));
        result = (result || GherkinLineType.THEN.equals(lineType));
        return result;
    }
}
