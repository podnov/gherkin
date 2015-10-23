package com.evanzeimet.gherkin.formatter;

public class GherkinFormatterException extends Exception {

    private static final long serialVersionUID = 1062844629879019848L;

    public GherkinFormatterException(String message) {
        super(message);
    }

    public GherkinFormatterException(Throwable cause) {
        super(cause);
    }

    public GherkinFormatterException(String message, Throwable cause) {
        super(message, cause);
    }
}
