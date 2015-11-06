package com.evanzeimet.gherkin.formatter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

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
        formatter = spy(formatter);
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
    public void diffOutputTarget_different() throws GherkinFormatterException {
        File outputFile = mock(File.class);

        String newContents = "a";
        String currentContents = "b";

        doReturn(currentContents).when(formatter).readFile(outputFile);

        boolean actual = formatter.diffOutputTarget(newContents, outputFile);

        assertTrue(actual);
    }

    @Test
    public void diffOutputTarget_notDifferent() throws GherkinFormatterException {
        File outputFile = mock(File.class);

        String newContents = "a";
        String currentContents = "a";

        doReturn(currentContents).when(formatter).readFile(outputFile);

        boolean actual = formatter.diffOutputTarget(newContents, outputFile);

        assertFalse(actual);
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
