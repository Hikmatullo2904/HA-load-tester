package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMetrics {

    private String stepId;          // which RequestStep this belongs to
    private long startTimeMs;       // epoch millis
    private long endTimeMs;         // epoch millis

    private int statusCode;         // HTTP 200, 500, 404 etc.
    private boolean success;        // based on ValidationRules

    private String errorType;       // timeout, connection_error, status_500...

    private long latencyMs;         // endTimeMs - startTimeMs

    private long bytesSent;
    private long bytesReceived;

    /**
     * Short human-friendly error message or snippet.
     * Examples:
     *  - "Database timeout"
     *  - "Unauthorized"
     *  - "Request timed out after 3000ms"
     * Keep this small when filling (e.g., truncate to 200 chars).
     */
    private String errorMessage;
}
