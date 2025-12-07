package uz.hikmatullo.loadtesting.engine.executors;

import lombok.extern.slf4j.Slf4j;
import uz.hikmatullo.loadtesting.engine.context.ExecutionContext;
import uz.hikmatullo.loadtesting.model.entity.LoadTest;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.entity.metrics.RequestMetrics;
import uz.hikmatullo.loadtesting.util.ExtractionRuleUtil;
import uz.hikmatullo.loadtesting.util.HttpRequestUtil;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Ultra-fast Virtual User runner.
 * - Minimal per-request work
 * - Uses async HTTP I/O (sendAsync + join)
 * - Converts response body to String ONLY when needed
 * - Local buffering and batch flush to global queue
 * - NO heavy post-processing (classification/error-key building) here
 */
@Slf4j
public class VirtualUserRunner implements Runnable {

    private static final int LOCAL_FLUSH_THRESHOLD = 256;

    private final LoadTest loadTest;
    private final Queue<RequestMetrics> globalCollector;
    private final AtomicBoolean stopFlag;
    private final HttpClient sharedClient;

    public VirtualUserRunner(LoadTest loadTest,
                             Queue<RequestMetrics> globalCollector,
                             AtomicBoolean stopFlag,
                             HttpClient sharedClient) {
        this.loadTest = loadTest;
        this.globalCollector = globalCollector;
        this.stopFlag = stopFlag;
        this.sharedClient = sharedClient;
    }

    @Override
    public void run() {
        // Per-VU state
        ExecutionContext ctx = new ExecutionContext();
        List<RequestMetrics> localBuffer = new ArrayList<>(LOCAL_FLUSH_THRESHOLD * 2);

        var steps = loadTest.getSteps();

        loop:
        while (!stopFlag.get()) {
            for (RequestStep step : steps) {
                if (stopFlag.get()) break loop;

                long startTimeMs = System.currentTimeMillis();

                try {
                    HttpRequest req = HttpRequestUtil.buildRequest(step, ctx);

                    // Async non-blocking I/O, underlying client handles async; join keeps sequential semantics.
                    CompletableFuture<HttpResponse<byte[]>> cf =
                            sharedClient.sendAsync(req, HttpResponse.BodyHandlers.ofByteArray());

                    HttpResponse<byte[]> resp = cf.join();

                    long endTimeMs = System.currentTimeMillis();

                    int status = resp.statusCode();
                    boolean success = status >= 200 && status < 400;

                    byte[] bodyBytes = resp.body();
                    int bytesReceived = bodyBytes != null ? bodyBytes.length : 0;

                    // Only convert body to String when needed (error or extraction rules)
                    String bodyStr = null;
                    boolean needsBodyString = !success || !step.getExtractionRules().isEmpty();
                    if (needsBodyString && bytesReceived > 0) {
                        bodyStr = new String(bodyBytes, StandardCharsets.UTF_8);
                    }

                    // Build minimal RequestMetrics (no classification, no heavy keys)
                    RequestMetrics metric = RequestMetrics.builder()
                            .stepId(step.getId())
                            .startTimeMs(startTimeMs)
                            .endTimeMs(endTimeMs)
                            .latencyMs(endTimeMs - startTimeMs)
                            .statusCode(status)
                            .success(success)
                            .errorType(success ? null : "http_" + status)
                            .bytesSent(step.getBody() != null ? step.getBody().length() : 0)
                            .bytesReceived(bytesReceived)

                            .errorMessage( !success ? getErrorMessage(bodyStr, true) : null )
                            .build();

                    localBuffer.add(metric);

                    // If not success -> stop executing next steps for this VU iteration (but keep VU alive)
                    if (!success) {
                        // run no extraction rules; flush and continue with next iteration
                        if (localBuffer.size() >= LOCAL_FLUSH_THRESHOLD) flushLocal(localBuffer);
                        break;
                    }

                    // Extraction rules
                    if (!step.getExtractionRules().isEmpty()) {
                        for (var rule : step.getExtractionRules()) {
                            try {
                                // bodyStr guaranteed non-null if extraction rules exist and there was content
                                ExtractionRuleUtil.applyRule(rule, bodyStr, ctx);
                            } catch (Exception ex) {
                                RequestMetrics errMetric = RequestMetrics.builder()
                                        .stepId(step.getId())
                                        .startTimeMs(startTimeMs)
                                        .endTimeMs(endTimeMs)
                                        .latencyMs(endTimeMs - startTimeMs)
                                        .success(false)
                                        .bytesSent(step.getBody() != null ? step.getBody().length() : 0)
                                        .bytesReceived(bytesReceived)
                                        .statusCode(status)
                                        .errorType("extraction_error")
                                        .errorMessage(getErrorMessage(ex.getMessage(), false))
                                        .build();

                                localBuffer.add(errMetric);
                                break;
                            }
                        }
                    }

                    if (localBuffer.size() >= LOCAL_FLUSH_THRESHOLD) flushLocal(localBuffer);

                } catch (Exception e) {
                    long endTimeMs = System.currentTimeMillis();

                    RequestMetrics errMetric = RequestMetrics.builder()
                            .stepId(step.getId())
                            .startTimeMs(startTimeMs)
                            .endTimeMs(endTimeMs)
                            .latencyMs(startTimeMs - endTimeMs)
                            .statusCode(500)
                            .success(false)
                            .errorType(null)
                            .bytesSent(step.getBody() != null ? step.getBody().length() : 0)
                            .bytesReceived(0)
                            .errorMessage(getErrorMessage(e.getMessage(), false))
                            .build();

                    localBuffer.add(errMetric);

                    if (localBuffer.size() >= LOCAL_FLUSH_THRESHOLD) flushLocal(localBuffer);
                    // stop this iteration of steps (keep VU alive)
                    break;
                }
            }
        }

        // final flush
        if (!localBuffer.isEmpty()) flushLocal(localBuffer);
    }

    private String getErrorMessage(String bodyStr, boolean errorFromTarget) {
        if(bodyStr == null && errorFromTarget) {
            return "Unknown error from target";
        }else if(bodyStr == null) {
            return "Unexpected error happened";
        }
        return bodyStr;
    }

    private void flushLocal(List<RequestMetrics> localBuffer) {
        globalCollector.addAll(localBuffer);
        localBuffer.clear();
    }
}
