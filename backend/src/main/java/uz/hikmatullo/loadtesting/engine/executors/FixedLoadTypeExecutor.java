package uz.hikmatullo.loadtesting.engine.executors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.engine.aggregator.SimpleMetricsAggregator;
import uz.hikmatullo.loadtesting.model.entity.LoadTest;
import uz.hikmatullo.loadtesting.model.entity.metrics.RequestMetrics;
import uz.hikmatullo.loadtesting.model.entity.metrics.TestExecutionReport;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedLoadTypeExecutor {

    private final SimpleMetricsAggregator simpleMetricsAggregator;

    /**
     * Run a load test and return the final TestExecutionReport.
     */
    public TestExecutionReport run(LoadTest loadTest) {
        ExecutionResult result = execute(loadTest);
        log.info("Test started At: {}", result.startedAt());
        List<RequestMetrics> metrics = List.copyOf(result.metrics());
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

    /**
     * Execute fixed VUs for durationSeconds.
     * Optimized: uses ConcurrentLinkedQueue and virtual-thread executor factory.
     */
    public ExecutionResult execute(LoadTest loadTest) {

        int users = loadTest.getProfile().getVirtualUsers();
        int durationSeconds = loadTest.getProfile().getDurationSeconds();

        log.info("Running FIXED load test: {} VUs for {} seconds", users, durationSeconds);

        long startedAt = System.currentTimeMillis();

        // --- fast concurrent collector (low contention)
        Queue<RequestMetrics> allMetrics = new ConcurrentLinkedQueue<>();

        HttpClient SHARED_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

        // --- virtual thread executor (uses Virtual Threads)
        ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());

        // --- stop flag for VUs
        AtomicBoolean stopFlag = new AtomicBoolean(false);

        // schedule shutdown (graceful)
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> stopFlag.set(true), durationSeconds, TimeUnit.SECONDS);

        List<Future<?>> futures = new ArrayList<>(users);

        // spawn virtual users (each VU will use local buffers and flush into allMetrics)
        for (int i = 0; i < users; i++) {
            futures.add(executor.submit(new VirtualUserRunner(loadTest, allMetrics, stopFlag, SHARED_CLIENT)));
        }

        // wait for all VUs to finish
        for (Future<?> f : futures) {
            try {
                f.get();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException ignored) {
                // ignore individual VU exceptions; metrics contain failures
            }
        }

        // shutdown executors
        executor.shutdownNow();
        scheduler.shutdownNow();

        long finishedAt = System.currentTimeMillis();

        log.info("FIXED load completed. Total metrics collected = {}", allMetrics.size());

        // convert queue -> list for result
        List<RequestMetrics> metricsList = new ArrayList<>(allMetrics.size());
        metricsList.addAll(allMetrics);

        return new ExecutionResult(metricsList, startedAt, finishedAt);
    }
}
