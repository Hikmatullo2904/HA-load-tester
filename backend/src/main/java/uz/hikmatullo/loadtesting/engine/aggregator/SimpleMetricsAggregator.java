package uz.hikmatullo.loadtesting.engine.aggregator;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.entity.metrics.*;
import uz.hikmatullo.loadtesting.util.PercentileCalculator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for converting raw RequestMetrics (one entry per HTTP request)
 * into high-level load-test insights:
 * - GlobalMetrics  → summary of entire test run
 * - StepMetrics    → per-request-step statistics (e.g., Login step, Search step)
 * - Timeline       → downsampled second-by-second RPS & error graph
 * This class does NOT care how tests are executed. It only consumes metrics
 * and produces a structured report.
 */
@Component
public class SimpleMetricsAggregator {

    private static final int MAX_TIMELINE_POINTS = 300;

    /**
     * Entry point. Takes raw request metrics and produces the final
     * TestExecutionReport object returned to the caller.
     * Steps:
     * 1. Build global summary (success rate, percentiles, RPS…)
     * 2. Build step-level breakdown (latency per step, errors per step…)
     * 3. Build timeline (RPS over time, success/failure over time)
     * The returned report is what the frontend or API consumers visualize.
     */
    public TestExecutionReport buildReport(
            String testId,
            long startedAt,
            long finishedAt,
            List<RequestMetrics> metrics,
            List<RequestStep> steps
    ) {

        addErrorDetails(metrics);

        GlobalMetrics global = buildGlobalMetrics(metrics, startedAt, finishedAt);
        List<StepMetrics> perStep = buildStepMetrics(metrics, steps, startedAt, finishedAt);
        List<TimelinePoint> timeline = buildTimeline(metrics, startedAt, finishedAt);

        return TestExecutionReport.builder()
                .testId(testId)
                .global(global)
                .steps(perStep)
                .timeline(timeline)
                .startedAt(startedAt)
                .finishedAt(finishedAt)
                .build();
    }


    private GlobalMetrics buildGlobalMetrics(List<RequestMetrics> metrics, long startedAt, long finishedAt) {

        long total = metrics.size();
        long success = metrics.stream().filter(RequestMetrics::isSuccess).count();
        long failed = total - success;

        long durationSec = Math.max(1, (finishedAt - startedAt) / 1000);

        List<Long> latencies = metrics.stream()
                .map(RequestMetrics::getLatencyMs)
                .toList();

        return GlobalMetrics.builder()
                .totalRequests(total)
                .successfulRequests(success)
                .failedRequests(failed)
                .successRate(total == 0 ? 0 : (success * 100.0 / total))
                .errorRate(total == 0 ? 0 : (failed * 100.0 / total))

                .minLatency(latencies.stream().min(Long::compare).orElse(0L))
                .maxLatency(latencies.stream().max(Long::compare).orElse(0L))
                .meanLatency((long) latencies.stream().mapToLong(Long::longValue).average().orElse(0))

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
                .values()
                .stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
    }


    private List<StepMetrics> buildStepMetrics(
            List<RequestMetrics> metrics,
            List<RequestStep> steps,
            long startedAt,
            long finishedAt
    ) {
        long durationSec = Math.max(1, (finishedAt - startedAt) / 1000);

        Map<String, String> stepNames = steps.stream()
                .collect(Collectors.toMap(RequestStep::getId, RequestStep::getName));

        Map<String, List<RequestMetrics>> grouped =
                metrics.stream().collect(Collectors.groupingBy(RequestMetrics::getStepId));

        List<StepMetrics> result = new ArrayList<>();

        for (var entry : grouped.entrySet()) {
            String stepId = entry.getKey();
            List<RequestMetrics> list = entry.getValue();

            long total = list.size();
            long success = list.stream().filter(RequestMetrics::isSuccess).count();
            long failed = total - success;

            List<Long> latencies = list.stream().map(RequestMetrics::getLatencyMs).toList();

            Map<Integer, Long> statusDist = list.stream()
                    .collect(Collectors.groupingBy(
                            RequestMetrics::getStatusCode,
                            Collectors.counting()
                    ));

            Map<String, Long> errorDist = list.stream()
                    .filter(r -> !r.isSuccess())
                    .collect(Collectors.groupingBy(
                            RequestMetrics::getErrorType,
                            Collectors.counting()
                    ));

            List<StepErrorDetail> errorDetailsList = getStepErrorDetails(list);

            result.add(
                    StepMetrics.builder()
                            .stepId(stepId)
                            .stepName(stepNames.get(stepId))

                            .totalRequests(total)
                            .successfulRequests(success)
                            .failedRequests(failed)

                            .successRate(total == 0 ? 0 : success * 100.0 / total)
                            .errorRate(total == 0 ? 0 : failed * 100.0 / total)

                            .minLatency(latencies.stream().min(Long::compare).orElse(0L))
                            .maxLatency(latencies.stream().max(Long::compare).orElse(0L))
                            .meanLatency((long) latencies.stream().mapToLong(Long::longValue).average().orElse(0))

                            .p50(PercentileCalculator.p50(latencies))
                            .p90(PercentileCalculator.p90(latencies))
                            .p95(PercentileCalculator.p95(latencies))
                            .p99(PercentileCalculator.p99(latencies))

                            .rpsAverage(total / (double) durationSec)
                            .rpsPeak(0) // optional

                            .statusCodeDistribution(statusDist)
                            .errorDistribution(errorDist)
                            .errorDetails(errorDetailsList)
                            .build()
            );
        }

        return result;
    }

    private List<StepErrorDetail> getStepErrorDetails(List<RequestMetrics> list) {
        Map<String, StepErrorDetail> distinctErrors = new HashMap<>();

        for (RequestMetrics rm : list) {
            if (!rm.isSuccess()) {

                // build grouping key: statusCode + errorMessage snippet
                int code = rm.getStatusCode();

                String key = code + ":" + rm.getErrorMessage();

                distinctErrors.compute(key, (k, v) -> {
                    if (v == null) {
                        return StepErrorDetail.builder()
                                .statusCode(code)
                                .count(1)
                                .message(rm.getErrorMessage())
                                .build();
                    } else {
                        v.setCount(v.getCount() + 1);
                        return v;
                    }
                });
            }
        }

        List<StepErrorDetail> stepErrorDetails = new ArrayList<>(distinctErrors.values());
        if (stepErrorDetails.size() > 30) {
            stepErrorDetails = stepErrorDetails.stream()
                    .limit(30)
                    .toList();
        }
        return stepErrorDetails;
    }

    /**
     * Produces a simplified (downsampled) timeline of the test.
     * Why downsample?
     * - Storing data for every second is too heavy for long tests
     * - The frontend becomes slow with thousands of points
     * - Humans can't read 3,600+ seconds of data anyway
     * How it works:
     * 1. Compute bucket size so total points <= MAX_TIMELINE_POINTS (e.g., 300)
     * Example:
     * 900-second test → bucket size 3 seconds
     * 3600-second test → bucket size 12 seconds
     * 2. For each bucket, we aggregate:
     * - number of requests
     * - successes
     * - failures
     * - RPS (requests / bucketSize)
     * The timeline powers graphs like:
     * - RPS over time
     * - Errors over time
     * - Test stability across different phases
     */
    private List<TimelinePoint> buildTimeline(List<RequestMetrics> metrics, long startedAt, long finishedAt) {

        long durationSec = Math.max(1, (finishedAt - startedAt) / 1000);
        int bucketSize = (int) Math.max(1, durationSec / (double) MAX_TIMELINE_POINTS);

        // bucketKey = (secondSinceStart / bucketSize)
        Map<Long, TimelineBucket> buckets = new HashMap<>();

        for (RequestMetrics m : metrics) {
            long second = (m.getStartTimeMs() / 1000) - (startedAt / 1000);
            long bucket = second / bucketSize;

            TimelineBucket acc = buckets.computeIfAbsent(bucket, k -> new TimelineBucket());
            acc.requests++;
            if (m.isSuccess()) acc.successes++;
            else acc.failures++;
        }

        List<TimelinePoint> result = new ArrayList<>();
        for (var e : buckets.entrySet()) {
            long bucketIndex = e.getKey();
            TimelineBucket b = e.getValue();

            long ts = startedAt / 1000 + bucketIndex * bucketSize;

            result.add(
                    TimelinePoint.builder()
                            .timestampSecond(ts)
                            .requests(b.requests)
                            .successes(b.successes)
                            .failures(b.failures)
                            .rps(b.requests / (double) bucketSize)
                            .build()
            );
        }

        result.sort(Comparator.comparingLong(TimelinePoint::getTimestampSecond));
        return result;
    }

    // local accumulator
    private static class TimelineBucket {
        long requests = 0;
        long successes = 0;
        long failures = 0;
    }


    private void addErrorDetails(List<RequestMetrics> metrics) {
        for (RequestMetrics met : metrics) {
            if (!met.isSuccess()) {
                String errorMsg = met.getErrorMessage();
                String errorType = met.getErrorType();
                if (errorType == null) {
                    errorType = classifyError(errorMsg);
                }
                errorMsg = truncate(errorMsg, 200);

                met.setErrorType(errorType);
                met.setErrorMessage(errorMsg);
            }
        }
    }

    private static String truncate(String s, int maxLen) {
        if (s == null || maxLen <= 0) return null;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen) + "...";
    }

    private String classifyError(String msg) {
        if (msg == null) return "unknown_error";

        String lower = msg.toLowerCase();

        if (lower.contains("timeout")) return "timeout";
        if (lower.contains("connect") || lower.contains("connection")) return "connection_error";
        if (lower.contains("refused")) return "connection_refused";
        if (lower.contains("ssl") || lower.contains("tls")) return "tls_error";
        if (lower.contains("unresolved") || lower.contains("unknown host")) return "dns_error";

        return "exception";
    }
}
