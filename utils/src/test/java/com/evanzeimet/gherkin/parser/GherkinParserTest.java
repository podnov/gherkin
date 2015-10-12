package com.evanzeimet.gherkin.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.evanzeimet.gherkin.GherkinTestUtils;
import com.evanzeimet.gherkin.parser.GherkinParser;
import com.evanzeimet.gherkin.parser.GherkinParserException;
import com.evanzeimet.gherkin.parser.structure.GherkinFeature;
import com.fasterxml.jackson.core.JsonProcessingException;

public class GherkinParserTest {

    private GherkinParser parser;

    @Before
    public void setUp() throws Exception {
        parser = new GherkinParser();
    }

    @Test
    public void isAndLine() {
        String givenStringLine = "\t\t  And ";

        Boolean actual = parser.isAndLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isBackgroundHeaderLine() {
        String givenStringLine = "\t\t  Background: ";

        Boolean actual = parser.isBackgroundHeaderLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isCommentLine_emptyString() {
        String givenStringLine = "";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertFalse(actual);
    }

    @Test
    public void isCommentLine_dataTable() {
        String givenStringLine = " |";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertFalse(actual);
    }

    @Test
    public void isCommentLine_hashFirst() {
        String givenStringLine = "#";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isCommentLine_someText() {
        String givenStringLine = "    Given I like to party";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertFalse(actual);
    }

    @Test
    public void isCommentLine_spaceBeforeHash() {
        String givenStringLine = " #";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isCommentLine_spacesBeforeHash() {
        String givenStringLine = "     #";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isCommentLine_tabBeforeHash() {
        String givenStringLine = "\t#";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isCommentLine_tabsBeforeHash() {
        String givenStringLine = "\t\t\t#";

        Boolean actual = parser.isCommentLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isDataTableLine_emptyString() {
        String givenStringLine = "";

        Boolean actual = parser.isDataTableLine(givenStringLine);

        assertFalse(actual);
    }

    @Test
    public void isDataTableLine_pipeFirst() {
        String givenStringLine = "| column value |";

        Boolean actual = parser.isDataTableLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isDataTableLine_someText() {
        String givenStringLine = "    Given a non-data-table line";

        Boolean actual = parser.isDataTableLine(givenStringLine);

        assertFalse(actual);
    }

    @Test
    public void isDataTableLine_spaceBeforePipe() {
        String givenStringLine = " | column value |";

        Boolean actual = parser.isDataTableLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isDataTableLine_spacesBeforePipe() {
        String givenStringLine = "    | column value |";

        Boolean actual = parser.isDataTableLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isDataTableLine_tabBeforePipe() {
        String givenStringLine = "\t| column value |";

        Boolean actual = parser.isDataTableLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isDataTableLine_tabsBeforePipe() {
        String givenStringLine = "\t\t\t| column value |";

        Boolean actual = parser.isDataTableLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isFeatureHeaderLine() {
        String givenStringLine = "\t\t  Feature: ";

        Boolean actual = parser.isFeatureHeaderLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isGivenLine() {
        String givenStringLine = "\t\t  Given ";

        Boolean actual = parser.isGivenLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isScenarioHeaderLine() {
        String givenStringLine = "\t\t  Scenario: ";

        Boolean actual = parser.isScenarioHeaderLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isThenLine() {
        String givenStringLine = "\t\t  Then ";

        Boolean actual = parser.isThenLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void isWhenLine() {
        String givenStringLine = "\t\t  When ";

        Boolean actual = parser.isWhenLine(givenStringLine);

        assertTrue(actual);
    }

    @Test
    public void parse() throws GherkinParserException,
            JsonProcessingException {
        String filename = "GherkinParserTest_parse_given.feature";
        String givenFeatureContents = GherkinTestUtils.readRelativeResource(getClass(), filename);

        GherkinFeature actual = parser.parse(givenFeatureContents);

        String actualJson = GherkinTestUtils.stringify(actual);
        actualJson = GherkinTestUtils.dosToUnix(actualJson);

        filename = "GherkinParserTest_parse_expected.json";
        String expectedJson = GherkinTestUtils.readRelativeResource(getClass(), filename);
        expectedJson = GherkinTestUtils.dosToUnix(expectedJson);

        assertEquals(expectedJson, actualJson);
    }
}
