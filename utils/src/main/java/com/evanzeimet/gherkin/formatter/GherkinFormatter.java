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

    protected static final Pattern dataTableRowWhitespacePrefix = Pattern.compile("^(\\s*)\\|.*");

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

        ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> backgroundDataTableHeaderRowsMap = mapBackgroundDataTableRowsToTheirHeaders(feature);
        allDataTableHeaderRowsMap.putAll(backgroundDataTableHeaderRowsMap);

        ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> scenarioDataTableHeaderRowsMap = mapScenarioDataTableRowsToTheirHeaders(feature);
        allDataTableHeaderRowsMap.putAll(scenarioDataTableHeaderRowsMap);

        formatColumns(allDataTableHeaderRowsMap);
    }

    protected void formatColumns(ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> allDataTableHeaderRowsMap) {
        Iterator<GherkinDataTableRow> uniqueHeaders = allDataTableHeaderRowsMap.keySet().iterator();

        while (uniqueHeaders.hasNext()) {
            GherkinDataTableRow uniqueHeader = uniqueHeaders.next();
            List<GherkinDataTableRow> rows = allDataTableHeaderRowsMap.get(uniqueHeader);

            formatRowColumnWidths(uniqueHeader, rows);
            formatRowText(uniqueHeader, rows);
        }
    }

    protected void formatRowColumnWidths(GherkinDataTableRow uniqueHeader,
            List<GherkinDataTableRow> rows) {
        int columnCount = uniqueHeader.getColumns().size();

        for (int columnIndex = 0; columnIndex < columnCount; columnIndex++) {
            int maxLength = getColumnMaxWidth(rows, columnIndex);
            setColumnWidths(rows, columnIndex, maxLength);
        }
    }

    protected void formatRowText(GherkinDataTableRow uniqueHeader,
            List<GherkinDataTableRow> rows) {
        String prefix = getDataTableRowPrefix(uniqueHeader);

        for (GherkinDataTableRow row : rows) {
            List<String> rowColumns = row.getColumns();
            GherkinLine rowGherkinLine = row.getLine();

            String joinedColumns = StringUtils.join(rowColumns, " | ");
            String joinedColumnsWithPrefix = String.format("%s| %s |", prefix, joinedColumns);

            rowGherkinLine.setText(joinedColumnsWithPrefix);
        }
    }

    protected int getColumnMaxWidth(List<GherkinDataTableRow> rows, int columnIndex) {
        int result = 0;

        for (GherkinDataTableRow row : rows) {
            List<String> columns = row.getColumns();

            if (columns.size() > columnIndex) {
                String columnValue = columns.get(columnIndex);
                int columnValueLength = columnValue.length();

                result = Math.max(result, columnValueLength);
            }
        }

        return result;
    }

    protected String getDataTableRowPrefix(GherkinDataTableRow row) {
        String rowText = row.getLine().getText();
        Matcher matcher = dataTableRowWhitespacePrefix.matcher(rowText);
        String result;
        
        if (matcher.matches()) {
            result = matcher.group(1);
        } else {
            result = "";
        }

        return result;
    }

    protected ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> mapBackgroundDataTableRowsToTheirHeaders(GherkinFeature feature) {
        GherkinBackground background = feature.getBackground();
        List<GherkinLine> backgroundLines = background.getLines();

        return mapDataTableRowsToTheirHeaders(backgroundLines);
    }

    protected ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> mapDataTableRowsToTheirHeaders(List<GherkinLine> lines) {
        ArrayListMultimap<GherkinDataTableRow /* header */, GherkinDataTableRow /* body rows */> result = ArrayListMultimap.create();

        if (lines != null) {
            GherkinDataTableRow currentDataTableHeader = null;

            for (GherkinLine line : lines) {
                GherkinLineType lineType = line.getLineType();

                boolean isComment = GherkinLineType.COMMENT.equals(lineType);

                boolean lineAffectsDataTableFlow = (!isComment);

                if (lineAffectsDataTableFlow) {
                    boolean lineIsDataTablePart = GherkinLineType.isDataTable(lineType);

                    if (lineIsDataTablePart) {
                        GherkinDataTableRow currentRow = createDataTableRow(line);
                        GherkinLineType effectiveLineType = line.getEffectiveLineType();

                        boolean lineIsDataTableHeader = GherkinLineType.DATA_TABLE_HEADER.equals(effectiveLineType);

                        if (lineIsDataTableHeader) {
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

    protected ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> mapScenarioDataTableRowsToTheirHeaders(GherkinFeature feature) {
        List<GherkinScenario> scenarios = feature.getScenarios();
        return mapScenarioDataTableRowsToTheirHeaders(scenarios);
    }

    protected ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> mapScenarioDataTableRowsToTheirHeaders(List<GherkinScenario> scenarios) {
        ArrayListMultimap<GherkinDataTableRow /* header */, GherkinDataTableRow /* body rows */> result = ArrayListMultimap.create();

        if (scenarios != null) {
            for (GherkinScenario scenario : scenarios) {
                List<GherkinLine> lines = scenario.getLines();

                ArrayListMultimap<GherkinDataTableRow, GherkinDataTableRow> dataTableHeaderRowsMap = mapDataTableRowsToTheirHeaders(lines);

                result.putAll(dataTableHeaderRowsMap);
            }
        }

        return result;
    }

    protected void setColumnWidths(List<GherkinDataTableRow> rows, int columnIndex, int maxLength) {
        for (GherkinDataTableRow row : rows) {
            List<String> columns = row.getColumns();

            if (columns.size() > columnIndex) {
                String columnValue = columns.get(columnIndex);
                columnValue = StringUtils.rightPad(columnValue, maxLength, " ");

                columns.set(columnIndex, columnValue);
            }
        }
    }
}
