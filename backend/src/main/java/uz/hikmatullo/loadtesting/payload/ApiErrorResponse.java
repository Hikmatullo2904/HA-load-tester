package uz.hikmatullo.loadtesting.payload;

import java.time.LocalDateTime;

public class ApiErrorResponse {
    private final String message;
    private final LocalDateTime timestamp;

    public ApiErrorResponse(String message) {
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    public ApiErrorResponse(String message, LocalDateTime timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
