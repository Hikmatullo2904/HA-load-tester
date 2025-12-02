package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepMetrics {

    private String stepId;
    private String stepName;

    private long totalRequests;
    private long successfulRequests;
    private long failedRequests;

    private double successRate;
    private double errorRate;

    private long minLatency;
    private long maxLatency;
    private long meanLatency;

    private long p50;
    private long p90;
    private long p95;
    private long p99;

    private double rpsAverage;
    private double rpsPeak;

    private Map<Integer, Long> statusCodeDistribution; // 200 -> 1234, etc.
    private Map<String, Long> errorDistribution;       // timeout -> 45, status_500 -> 12
    /**
     * Distinct error list for this step.
     * Each item represents a unique error signature:
     *   (statusCode + message snippet)
     *
     * Only 1 entry per unique error. Very lightweight.
     */
    private List<StepErrorDetail> errorDetails = new ArrayList<>();
}
