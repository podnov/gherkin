package com.evanzeimet.gherkin.formatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.evanzeimet.gherkin.parser.structure.GherkinBackground;
import com.evanzeimet.gherkin.parser.structure.GherkinFeature;
import com.evanzeimet.gherkin.parser.structure.GherkinLine;
import com.evanzeimet.gherkin.parser.structure.GherkinLineType;
import com.evanzeimet.gherkin.parser.structure.GherkinScenario;
import com.google.common.collect.ArrayListMultimap;

public class GherkinFormatter {

    protected static final Pattern uniqueHeaderWhitespacePrefix = Pattern.compile("^(\\s*)\\|.*");

    protected GherkinDataTableRow createDataTableRow(GherkinLine gherkinLine) {
        String text = gherkinLine.getText().trim();
        String[] splits = text.split("\\|");

        int splitCount = splits.length;
        // there will always be an blank split before the first pipe
        int firstColumnIndex = 1;
        int columnCount = (splitCount - firstColumnIndex);

        List<String> columns = new ArrayList<String>(columnCount);

        for (int splitIndex = firstColumnIndex; splitIndex < splitCount; splitIndex++) {
            String split = splits[splitIndex];
            String columnName = split.trim();
            columns.add(columnName);
        }

        GherkinDataTableRow result = new GherkinDataTableRow();

        result.setColumms(columns);
        result.setLine(gherkinLine);

        return result;
    }

    public void format(GherkinFeature feature) {
        ArrayListMultimap<GherkinDataTableRow /* header */, GherkinDataTableRow /* body rows */> allDataTableHeaderRowsMap = ArrayListMultimap.create();

        GherkinBackground background = feature.getBackground();
        List<GherkinLine> lines = background.getLines();

        ArrayListMultimap<GherkinDataTableRow /* header */, GherkinDataTableRow /* body rows */> currentDataTableHeaderRowsMap = mapDataTableRowsToHeaders(lines);
        allDataTableHeaderRowsMap.putAll(currentDataTableHeaderRowsMap);

        List<GherkinScenario> scenarios = feature.getScenarios();

        currentDataTableHeaderRowsMap = mapScenarioDataTableRowsToHeaders(scenarios);
        allDataTableHeaderRowsMap.putAll(currentDataTableHeaderRowsMap);

        formatColumns(allDataTableHeaderRowsMap);
    }

    protected void formatColumns(ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> allDataTableHeaderRowsMap) {
        Iterator<GherkinDataTableRow> uniqueHeaders = allDataTableHeaderRowsMap.keySet().iterator();

        while (uniqueHeaders.hasNext()) {
            GherkinDataTableRow uniqueHeader = uniqueHeaders.next();
            List<GherkinDataTableRow> rows = allDataTableHeaderRowsMap.get(uniqueHeader);

            formatColumnsForHeader(uniqueHeader, rows);
            formatRowsForHeaderAndColumns(uniqueHeader, rows);
        }
    }

    protected void formatColumnsForHeader(GherkinDataTableRow uniqueHeader,
            List<GherkinDataTableRow> rows) {
        int columnCount = uniqueHeader.getColumns().size();

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            int maxLength = 0;

            for (GherkinDataTableRow row : rows) {
                String columnValue = row.getColumns().get(columnIndex);
                int columnValueLength = columnValue.length();
                maxLength = Math.max(maxLength, columnValueLength);
            }

            for (GherkinDataTableRow row : rows) {
                List<String> rowColumns = row.getColumns();

                String columnValue = rowColumns.get(columnIndex);
                columnValue = StringUtils.rightPad(columnValue, maxLength, " ");

                rowColumns.set(columnIndex, columnValue);
            }
        }
    }

    protected void formatRowsForHeaderAndColumns(GherkinDataTableRow uniqueHeader,
            List<GherkinDataTableRow> rows) {
        String uniqueHeaderText = uniqueHeader.getLine().getText();
        Matcher matcher = uniqueHeaderWhitespacePrefix.matcher(uniqueHeaderText);
        String prefix;
        
        if (matcher.matches()) {
            prefix = matcher.group(1);
        } else {
            prefix = "";
        }

        for (GherkinDataTableRow row : rows) {
            List<String> rowColumns = row.getColumns();
            GherkinLine rowGherkinLine = row.getLine();

            String newLineText = StringUtils.join(rowColumns, " | ");
            newLineText = String.format("%s| %s |", prefix, newLineText);

            rowGherkinLine.setText(newLineText);
        }
    }

    protected ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> mapDataTableRowsToHeaders(List<GherkinLine> lines) {
        ArrayListMultimap<GherkinDataTableRow /* header */, GherkinDataTableRow /* body rows */> result = ArrayListMultimap.create();

        if (lines != null) {
            GherkinDataTableRow currentDataTableHeader = null;

            for (GherkinLine line : lines) {
                GherkinLineType lineType = line.getLineType();

                boolean isNotComment = !GherkinLineType.COMMENT.equals(lineType);

                if (isNotComment) {
                    if (GherkinLineType.isDataTable(lineType)) {
                        GherkinDataTableRow currentRow = createDataTableRow(line);
                        GherkinLineType effectiveLineType = line.getEffectiveLineType();

                        if (GherkinLineType.DATA_TABLE_HEADER.equals(effectiveLineType)) {
                            currentDataTableHeader = currentRow;
                        }

                        result.put(currentDataTableHeader, currentRow);
                    } else {
                        currentDataTableHeader = null;
                    }
                }
            }
        }

        return result;
    }

    protected ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> mapScenarioDataTableRowsToHeaders(List<GherkinScenario> scenarios) {
        ArrayListMultimap<GherkinDataTableRow /* header */, GherkinDataTableRow /* body rows */> result = ArrayListMultimap.create();

        if (scenarios != null) {
            for (GherkinScenario scenario : scenarios) {
                List<GherkinLine> lines = scenario.getLines();

                ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> dataTableHeaderRowsMap = mapDataTableRowsToHeaders(lines);

                result.putAll(dataTableHeaderRowsMap);
            }
        }

        return result;
    }
}
