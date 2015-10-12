package com.evanzeimet.gherkin.formatter;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.evanzeimet.gherkin.GherkinTestUtils;
import com.evanzeimet.gherkin.formatter.GherkinDataTableRow;
import com.evanzeimet.gherkin.formatter.GherkinFormatter;
import com.evanzeimet.gherkin.parser.structure.GherkinFeature;
import com.evanzeimet.gherkin.parser.structure.GherkinLine;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class GherkinFormatterTest {

    private GherkinFormatter formatter;

    @Before
    public void setUp() {
        formatter = new GherkinFormatter();
    }

    @Test
    public void createDataTableHeader_leadingSpaces() {
        String stringLine = "   | column 1 | column2 | column3 | column 4 |";

        GherkinLine gherkinLine = new GherkinLine();
        gherkinLine.setText(stringLine);

        GherkinDataTableRow actualDataTableHeader = formatter.createDataTableRow(gherkinLine);

        List<String> actualColumns = actualDataTableHeader.getColumns();

        assertThat(actualColumns, contains("column 1", "column2", "column3", "column 4"));
    }

    @Test
    public void createDataTableHeader_multipleColumnsColumn() {
        String stringLine = "   | column 1 | column2 | column3    |    column 4 |   ";

        GherkinLine gherkinLine = new GherkinLine();
        gherkinLine.setText(stringLine);

        GherkinDataTableRow actualDataTableHeader = formatter.createDataTableRow(gherkinLine);

        List<String> actualColumns = actualDataTableHeader.getColumns();

        assertThat(actualColumns, contains("column 1", "column2", "column3", "column 4"));
    }

    @Test
    public void createDataTableHeader_oneColumn() {
        String stringLine = "| column1 |";

        GherkinLine gherkinLine = new GherkinLine();
        gherkinLine.setText(stringLine);

        GherkinDataTableRow actualDataTableHeader = formatter.createDataTableRow(gherkinLine);

        List<String> actualColumns = actualDataTableHeader.getColumns();

        assertThat(actualColumns, contains("column1"));
    }

    @Test
    public void createDataTableHeader_trailingSpaces() {
        String stringLine = "| column 1 | column2 | column3 | column 4 |   ";

        GherkinLine gherkinLine = new GherkinLine();
        gherkinLine.setText(stringLine);

        GherkinDataTableRow actualDataTableHeader = formatter.createDataTableRow(gherkinLine);

        List<String> actualColumns = actualDataTableHeader.getColumns();

        assertThat(actualColumns, contains("column 1", "column2", "column3", "column 4"));
    }

    @Test
    public void format() throws JsonParseException,
            JsonMappingException,
            IOException {
        String filename = "GherkinFormatterTest_format_given.json";
        String givenFeatureJson = GherkinTestUtils.readRelativeResource(getClass(), filename);
        GherkinFeature givenFeature = GherkinTestUtils.objectify(givenFeatureJson,
                GherkinFeature.class);

        formatter.format(givenFeature);

        GherkinFeature actual = givenFeature;

        String actualJson = GherkinTestUtils.stringify(actual);
        actualJson = GherkinTestUtils.dosToUnix(actualJson);

        filename = "GherkinFormatterTest_format_expected.json";
        String expectedJson = GherkinTestUtils.readRelativeResource(getClass(), filename);
        expectedJson = GherkinTestUtils.dosToUnix(expectedJson);

        assertEquals(expectedJson, actualJson);
    }
}
