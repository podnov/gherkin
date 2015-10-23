package com.evanzeimet.gherkin.parser.structure;

public class GherkinLine {

    private GherkinLineType effectiveLineType;
    private int lineNumber;
    private GherkinLineType lineType;
    private String text;

    public GherkinLine() {

    }

    public GherkinLineType getEffectiveLineType() {
        return effectiveLineType;
    }

    public void setEffectiveLineType(GherkinLineType effectiveLineType) {
        this.effectiveLineType = effectiveLineType;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public GherkinLineType getLineType() {
        return lineType;
    }

    public void setLineType(GherkinLineType lineType) {
        this.lineType = lineType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
