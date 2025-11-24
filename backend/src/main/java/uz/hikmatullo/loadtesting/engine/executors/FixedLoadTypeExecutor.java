package uz.hikmatullo.loadtesting.engine.executors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.model.entity.LoadTest;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.entity.metrics.RequestMetrics;
import uz.hikmatullo.loadtesting.model.entity.metrics.TestExecutionReport;
import uz.hikmatullo.loadtesting.util.ExtractionRuleUtil;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class FixedLoadTypeExecutor {

    public TestExecutionReport run(LoadTest loadTest) {
        ExecutionResult result = execute(loadTest);
        log.info("Test started At: {}", result.startedAt());
        List<RequestMetrics> metrics = result.metrics();
        log.info("Total metrics collected = {}", metrics.size());

        log.info("Test finished At: {}", result.finishedAt());
        return  null;
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

                long st = System.currentTimeMillis();

                try {
                    HttpRequest request = buildHttpRequest(step, ctx);
                    HttpResponse<String> response = client.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

                    long end = System.currentTimeMillis();

                    RequestMetrics metric = buildMetric(step, response, st, end, true, null);
                    metricsCollector.add(metric);

                    // apply extraction rules
                    if (!step.getExtractionRules().isEmpty()) {
                        step.getExtractionRules().forEach(rule -> {
                            try {
                                ExtractionRuleUtil.applyRule(rule, response.body(), ctx);
                            } catch (Exception e) {
                                // user extraction rule error → stop for this user
                                metricsCollector.add(buildMetric(step, response, st, System.currentTimeMillis(), false, "extraction_error"));
                                return;
                            }
                        });
                    }

                } catch (Exception e) {
                    long end = System.currentTimeMillis();

                    metricsCollector.add(buildMetric(step, null, st, end, false, classifyError(e)));
                    return; // stop executing next steps for this VU
                }
            }
        }
    }

    /** Build HttpRequest from RequestStep */
    private HttpRequest buildHttpRequest(RequestStep step, ExecutionContext ctx) throws Exception {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(step.getUrl()))
                .timeout(Duration.ofMillis(step.getTimeoutMs()));

        step.getHeaders().forEach(b::header);

        switch (step.getMethod()) {
            case GET -> b.GET();
            case POST -> b.POST(HttpRequest.BodyPublishers.ofString(step.getBody() == null ? "" : step.getBody()));
            case PUT -> b.PUT(HttpRequest.BodyPublishers.ofString(step.getBody() == null ? "" : step.getBody()));
            case DELETE -> b.DELETE();
        }

        return b.build();
    }

    /** Build RequestMetrics */
    private RequestMetrics buildMetric(RequestStep step,
                                      HttpResponse<String> response,
                                      long start,
                                      long end,
                                      boolean success,
                                      String errorType) {

        return RequestMetrics.builder()
                .stepId(step.getId())
                .startTimeMs(start)
                .endTimeMs(end)
                .statusCode(response != null ? response.statusCode() : 0)
                .success(success)
                .errorType(errorType)
                .latencyMs(end - start)
                .bytesSent(step.getBody() != null ? step.getBody().length() : 0)
                .bytesReceived(response != null ? response.body().length() : 0)
                .build();
    }

    private String classifyError(Exception e) {
        String msg = e.getMessage();
        if (msg == null) return "unknown_error";
        if (msg.contains("timeout")) return "timeout";
        if (msg.contains("Connection")) return "connection_error";
        return "request_error";
    }
}
