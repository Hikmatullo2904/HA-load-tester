package uz.hikmatullo.loadtesting.exceptions;

public class ExtractionRuleException extends RuntimeException {

    public ExtractionRuleException(String message) {
        super(message);
    }

    public ExtractionRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
