package com.evanzeimet.gherkin.parser;

public class GherkinParserException extends Exception {

    private static final long serialVersionUID = 1062844629879019848L;

    public GherkinParserException(String message) {
        super(message);
    }

    public GherkinParserException(Throwable cause) {
        super(cause);
    }

    public GherkinParserException(String message, Throwable cause) {
        super(message, cause);
    }
}
