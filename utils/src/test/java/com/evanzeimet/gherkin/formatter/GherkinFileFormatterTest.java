package com.evanzeimet.gherkin.formatter;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.evanzeimet.gherkin.GherkinTestUtils;
import com.evanzeimet.gherkin.parser.GherkinParserException;

public class GherkinFileFormatterTest {

    private GherkinFileFormatter formatter;

    @Before
    public void setUp() {
        formatter = new GherkinFileFormatter();
    }

    @Test
    public void chooseLineSeparator_auto() {
        GherkinFormatterLineSeparator givenLineSeparator = GherkinFormatterLineSeparator.AUTO;

        formatter.setLineSeparator(givenLineSeparator);

        String actual = formatter.chooseLineSeparator();
        String expected = String.format("%n");

        assertEquals(expected, actual);
    }

    @Test
    public void chooseLineSeparator_null() {
        GherkinFormatterLineSeparator givenLineSeparator = null;

        formatter.setLineSeparator(givenLineSeparator);

        String actual = formatter.chooseLineSeparator();
        String expected = String.format("%n");

        assertEquals(expected, actual);
    }

    @Test
    public void chooseLineSeparator_unix() {
        GherkinFormatterLineSeparator givenLineSeparator = GherkinFormatterLineSeparator.UNIX;

        formatter.setLineSeparator(givenLineSeparator);

        String actual = formatter.chooseLineSeparator();
        String expected = "\n";

        assertEquals(expected, actual);
    }

    @Test
    public void chooseLineSeparator_windows() {
        GherkinFormatterLineSeparator givenLineSeparator = GherkinFormatterLineSeparator.WINDOWS;

        formatter.setLineSeparator(givenLineSeparator);

        String actual = formatter.chooseLineSeparator();
        String expected = "\r\n";

        assertEquals(expected, actual);
    }

    @Test
    public void format() throws GherkinParserException,
            IOException,
            GherkinFormatterException {
        String givenFilename = "GherkinFileFormatterTest_format_given.feature";
        File givenFile = GherkinTestUtils.getRelativeResource(getClass(), givenFilename);

        String outputFilenamePrefix = "GherkinFileFormatterTest_format_output";
        String outputfilenameSuffix = ".feature";
        File outputFile = File.createTempFile(outputFilenamePrefix, outputfilenameSuffix);

        formatter.formatFile(givenFile, outputFile);

        String expectedFilename = "GherkinFileFormatterTest_format_expected.feature";
        File expectedFile = GherkinTestUtils.getRelativeResource(getClass(), expectedFilename);

        String actual = GherkinTestUtils.readFile(outputFile);
        String expected = GherkinTestUtils.readFile(expectedFile);

        assertEquals(expected, actual);
    }
}
