package com.evanzeimet.gherkin.formatter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.evanzeimet.gherkin.parser.structure.GherkinLine;

public class GherkinDataTableRow {

    private List<String> columns = new ArrayList<String>();
    private GherkinLine line;

    public GherkinDataTableRow() {

    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumms(List<String> columns) {
        this.columns = columns;
    }

    public GherkinLine getLine() {
        return line;
    }

    public void setLine(GherkinLine line) {
        this.line = line;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        GherkinDataTableRow rhs = (GherkinDataTableRow) obj;
        return new EqualsBuilder()
                .append(columns, rhs.columns)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(9, 31)
                .append(columns)
                .toHashCode();
    }
}
