package uz.hikmatullo.loadtesting.engine.aggregator;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.metrics.*;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.util.PercentileCalculator;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class SimpleMetricsAggregator {

    public TestExecutionReport buildReport(
            String testId,
            long startedAt,
            long finishedAt,
            List<RequestMetrics> metrics,
            List<RequestStep> steps
    ) {
        GlobalMetrics global = buildGlobalMetrics(metrics, startedAt, finishedAt);
        List<StepMetrics> stepMetrics = buildStepMetrics(metrics, steps, startedAt, finishedAt);
        List<TimelinePoint> timeline = buildTimeline(metrics);

        return TestExecutionReport.builder()
                .testId(testId)
                .global(global)
                .steps(stepMetrics)
                .timeline(timeline)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .build();
    }

    // -------------------------------------------------------
    // GLOBAL METRICS
    // -------------------------------------------------------
    private GlobalMetrics buildGlobalMetrics(List<RequestMetrics> metrics, long startedAt, long finishedAt) {

        long total = metrics.size();
        long success = metrics.stream().filter(RequestMetrics::isSuccess).count();
        List<Long> latencies = metrics.stream()
                .map(RequestMetrics::getLatencyMs)
                .collect(Collectors.toList());

        long durationSec = Math.max(1, (finishedAt - startedAt) / 1000);

        return GlobalMetrics.builder()
                .totalRequests(total)
                .successfulRequests(success)
                .failedRequests(total - success)
                .successRate(total == 0 ? 0 : success * 100.0 / total)
                .errorRate(total == 0 ? 0 : (total - success) * 100.0 / total)
                .minLatency(latencies.stream().min(Long::compare).orElse(0L))
                .maxLatency(latencies.stream().max(Long::compare).orElse(0L))
                .meanLatency((long) latencies.stream().mapToLong(l -> l).average().orElse(0))

                .p50(PercentileCalculator.p50(latencies))
                .p90(PercentileCalculator.p90(latencies))
                .p95(PercentileCalculator.p95(latencies))
                .p99(PercentileCalculator.p99(latencies))

                .rpsAverage(total / (double) durationSec)
                .rpsPeak(computePeakRps(metrics))

                .testDurationSeconds(durationSec)
                .build();
    }

    private double computePeakRps(List<RequestMetrics> metrics) {
        return metrics.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getStartTimeMs() / 1000,
                        Collectors.counting()
                ))
                .values().stream()
                .mapToLong(v -> v)
                .max().orElse(0);
    }

    // -------------------------------------------------------
    // PER-STEP METRICS
    // -------------------------------------------------------
    private List<StepMetrics> buildStepMetrics(
            List<RequestMetrics> metrics,
            List<RequestStep> steps,
            long startedAt,
            long finishedAt
    ) {
        Map<String, String> stepNameMap = steps.stream()
                .collect(Collectors.toMap(RequestStep::getId, RequestStep::getName));

        Map<String, List<RequestMetrics>> grouped =
                metrics.stream().collect(Collectors.groupingBy(RequestMetrics::getStepId));

        long durationSec = Math.max(1, (finishedAt - startedAt) / 1000);

        List<StepMetrics> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            String stepId = entry.getKey();
            List<RequestMetrics> list = entry.getValue();

            List<Long> latencies = list.stream()
                    .map(RequestMetrics::getLatencyMs)
                    .collect(Collectors.toList());

            long total = list.size();
            long success = list.stream().filter(RequestMetrics::isSuccess).count();

            // Status codes
            Map<Integer, Long> statusCodeDist = new HashMap<>();
            list.forEach(m -> statusCodeDist.merge(m.getStatusCode(), 1L, Long::sum));

            // Errors
            Map<String, Long> errorDist = new HashMap<>();
            list.stream().filter(m -> !m.isSuccess())
                    .forEach(m -> errorDist.merge(m.getErrorType(), 1L, Long::sum));

            result.add(
                StepMetrics.builder()
                    .stepId(stepId)
                    .stepName(stepNameMap.get(stepId))
                    .totalRequests(total)
                    .successfulRequests(success)
                    .failedRequests(total - success)
                    .successRate(total == 0 ? 0 : success * 100.0 / total)
                    .errorRate(total == 0 ? 0 : (total - success) * 100.0 / total)
                    .minLatency(latencies.stream().min(Long::compare).orElse(0L))
                    .maxLatency(latencies.stream().max(Long::compare).orElse(0L))
                    .meanLatency((long) latencies.stream().mapToLong(x -> x).average().orElse(0))
                    .p50(PercentileCalculator.p50(latencies))
                    .p90(PercentileCalculator.p90(latencies))
                    .p95(PercentileCalculator.p95(latencies))
                    .p99(PercentileCalculator.p99(latencies))
                    .rpsAverage(total / (double) durationSec)
                    .rpsPeak(0) // optional
                    .statusCodeDistribution(statusCodeDist)
                    .errorDistribution(errorDist)
                    .build()
            );
        }

        return result;
    }

    // -------------------------------------------------------
    // TIMELINE
    // -------------------------------------------------------
    private List<TimelinePoint> buildTimeline(List<RequestMetrics> metrics) {

        Map<Long, List<RequestMetrics>> grouped =
            metrics.stream()
                .collect(Collectors.groupingBy(m -> m.getStartTimeMs() / 1000));

        List<TimelinePoint> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            long second = entry.getKey();
            List<RequestMetrics> list = entry.getValue();
            long total = list.size();
            long success = list.stream().filter(RequestMetrics::isSuccess).count();
            long fail = total - success;

            result.add(
                TimelinePoint.builder()
                    .timestampSecond(second)
                    .requests(total)
                    .successes(success)
                    .failures(fail)
                    .rps(total)
                    .build()
            );
        }

        result.sort(Comparator.comparingLong(TimelinePoint::getTimestampSecond));
        return result;
    }
}
