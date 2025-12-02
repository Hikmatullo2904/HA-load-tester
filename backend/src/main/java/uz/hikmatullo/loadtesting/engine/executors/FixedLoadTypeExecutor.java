package uz.hikmatullo.loadtesting.engine.executors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.engine.aggregator.SimpleMetricsAggregator;
import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.model.entity.LoadTest;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.entity.metrics.RequestMetrics;
import uz.hikmatullo.loadtesting.model.entity.metrics.TestExecutionReport;
import uz.hikmatullo.loadtesting.util.ExtractionRuleUtil;
import uz.hikmatullo.loadtesting.util.HttpRequestUtil;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedLoadTypeExecutor {

    private final SimpleMetricsAggregator simpleMetricsAggregator;

    public TestExecutionReport run(LoadTest loadTest) {
        ExecutionResult result = execute(loadTest);
        log.info("Test started At: {}", result.startedAt());
        List<RequestMetrics> metrics = result.metrics();
        log.info("Total metrics collected = {}", metrics.size());

        log.info("Test finished At: {}", result.finishedAt());
        return simpleMetricsAggregator.buildReport(
                loadTest.getId(),
                result.startedAt(),
                result.finishedAt(),
                metrics,
                loadTest.getSteps()
        );
    }


    public ExecutionResult execute(LoadTest loadTest) {

        int users = loadTest.getProfile().getVirtualUsers();
        int durationSeconds = loadTest.getProfile().getDurationSeconds();

        log.info("Running FIXED load test: {} VUs for {} seconds", users, durationSeconds);

        long startedAt = System.currentTimeMillis();

        // --- concurrent-safe collection for metrics
        List<RequestMetrics> allMetrics = new CopyOnWriteArrayList<>();

        // --- virtual thread executor
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        AtomicBoolean stopFlag = new AtomicBoolean(false);

        // schedule shutdown
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> stopFlag.set(true), durationSeconds, TimeUnit.SECONDS);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < users; i++) {
            futures.add(executor.submit(() ->
                    runSingleVirtualUser(loadTest, allMetrics, stopFlag)
            ));
        }

        // wait for all VUs to finish
        futures.forEach(f -> {
            try { f.get(); }
            catch (Exception ignored) {}
        });

        executor.shutdownNow();
        scheduler.shutdownNow();

        long finishedAt = System.currentTimeMillis();

        log.info("FIXED load completed. Total metrics collected = {}", allMetrics.size());

        return new ExecutionResult(allMetrics, startedAt, finishedAt);
    }

    /**
     * Runs steps in a loop until test duration expires.
     */
    private void runSingleVirtualUser(
            LoadTest loadTest,
            List<RequestMetrics> metricsCollector,
            AtomicBoolean stopFlag
    ) {
        ExecutionContext ctx = new ExecutionContext();
        HttpClient client = ctx.getHttpClient();

        while (!stopFlag.get()) {

            for (RequestStep step : loadTest.getSteps()) {

                if (stopFlag.get()) break;

                long start = System.currentTimeMillis();

                try {
                    HttpRequest request = HttpRequestUtil.buildRequest(step, ctx);

                    HttpResponse<String> response = client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

                    int status = response.statusCode();
                    boolean success = status >= 200 && status < 400;
                    long end = System.currentTimeMillis();

                    // Preparing a short snippet/message when there is an HTTP error
                    String httpErrorMsg = null;
                    if (!success) {
                        String body = response.body();
                        httpErrorMsg = truncate(body, 200); // 200 is max length
                    }

                    metricsCollector.add(buildMetric(
                            step,
                            response,
                            start,
                            end,
                            success,
                            success ? null : "http_" + status,
                            httpErrorMsg
                    ));

                    if (!success) {
                        System.out.println(response.body());
                        break;
                    }

                    for (var rule : step.getExtractionRules()) {
                        try {
                            ExtractionRuleUtil.applyRule(rule, response.body(), ctx);
                        } catch (Exception e) {
                            // capture extraction error message snippet
                            String extractionMsg = truncate(e.getMessage(), 200);

                            metricsCollector.add(buildMetric(
                                    step,
                                    response,
                                    start,
                                    System.currentTimeMillis(),
                                    false,
                                    "extraction_error",
                                    extractionMsg
                            ));
                            break;
                        }
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    long end = System.currentTimeMillis();

                    // capture exception message and pass to metric builder
                    String exMsg = truncate(e.getMessage(), 200);

                    metricsCollector.add(buildMetric(
                            step,
                            null,
                            start,
                            end,
                            false,
                            classifyError(e),
                            exMsg
                    ));

                    // Stop this iteration but keep VU alive
                    break;
                }
            }
        }
    }


    /*
     * Build RequestMetrics based on HTTP response or exception.
     * New parameter: errorMessageOverride - short message/snippet that helps to
     * create distinct errorKey and show representative message in reports.
     */
    private RequestMetrics buildMetric(RequestStep step,
                                       HttpResponse<String> response,
                                       long start,
                                       long end,
                                       boolean success,
                                       String errorType,
                                       String errorMessageOverride) {

        int statusCode = response != null ? response.statusCode() : 500;

        String responseBodySnippet = null;
        String errorMessage = null;

        // If there is a real HTTP response and it's an error, take a truncated snippet.
        if (response != null && !success) {
            String body = response.body();
            responseBodySnippet = truncate(body, 200);
            errorMessage = errorMessageOverride != null ? errorMessageOverride : responseBodySnippet;
        }

        // If there is no HTTP response (exception path) or extraction error,
        // use the provided override message (which may come from exception.getMessage()).
        if (response == null && errorMessageOverride != null) {
            errorMessage = errorMessageOverride;
        }

        // Fallback: if still null, use a generic message based on errorType
        if (errorMessage == null && errorType != null) {
            errorMessage = errorType;
        }

        // Build compact grouping key for distinct errors.
        String errorKey = null;
        if (!success) {
            errorKey = buildErrorKey(statusCode, errorType, errorMessage);
        }

        return RequestMetrics.builder()
                .stepId(step.getId())
                .startTimeMs(start)
                .endTimeMs(end)
                .statusCode(statusCode)
                .success(success)
                .errorType(errorType)
                .latencyMs(end - start)
                .bytesSent(step.getBody() != null ? step.getBody().length() : 0)
                .bytesReceived(response != null && response.body() != null ? response.body().length() : 0)

                .errorMessage(errorMessage)
                .responseBodySnippet(responseBodySnippet)
                .errorKey(errorKey)

                .build();
    }

    private static String truncate(String s, int maxLen) {
        if (s == null || maxLen <= 0) return null;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen) + "...";
    }

    private static String buildErrorKey(int statusCode, String errorType, String shortMessage) {
        String snippet = shortMessage != null ? shortMessage.replaceAll("\\s+", " ") : "";
        snippet = snippet.length() > 100 ? snippet.substring(0, 100) : snippet;

        if (statusCode > 0) {
            if (snippet.isEmpty()) return String.valueOf(statusCode);
            return statusCode + ":" + snippet;
        }

        if (errorType != null) {
            if (snippet.isEmpty()) return errorType;
            return errorType + ":" + snippet;
        }

        return snippet.isEmpty() ? "unknown_error" : snippet;
    }

    private String classifyError(Exception e) {
        if (e == null) return "unknown_error";

        String msg = e.getMessage();
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
