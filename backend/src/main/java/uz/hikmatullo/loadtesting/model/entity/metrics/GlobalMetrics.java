package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalMetrics {

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

    private long testDurationSeconds;
}
