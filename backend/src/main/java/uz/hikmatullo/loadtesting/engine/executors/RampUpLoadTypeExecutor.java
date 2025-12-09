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
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class RampUpLoadTypeExecutor {

    private final SimpleMetricsAggregator simpleMetricsAggregator;

    public TestExecutionReport run(LoadTest loadTest) {
        ExecutionResult result = execute(loadTest);
        return simpleMetricsAggregator.buildReport(
                loadTest.getId(),
                result.startedAt(),
                result.finishedAt(),
                result.metrics(),
                loadTest.getSteps()
        );
    }

    public ExecutionResult execute(LoadTest loadTest) {

        int targetUsers = loadTest.getProfile().getVirtualUsers();
        int rampSeconds = loadTest.getProfile().getRampUpSeconds();
        int durationSeconds = loadTest.getProfile().getDurationSeconds();

        log.info("Running RAMP-UP test: 0 → {} VUs over {} sec, total duration {} sec",
                targetUsers, rampSeconds, durationSeconds);

        long startedAt = System.currentTimeMillis();

        Queue<RequestMetrics> allMetrics = new ConcurrentLinkedQueue<>();
        HttpClient SHARED_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2).build();

        ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        AtomicBoolean stopFlag = new AtomicBoolean(false);

        // track number of spawned VUs
        AtomicInteger spawnedUsers = new AtomicInteger(0);

        // fractional spawn rate
        double usersPerSecond = (double) targetUsers / rampSeconds;

        List<Future<?>> vuFutures = new CopyOnWriteArrayList<>();

        // ========= RAMP-UP LOGIC ==========
        final long rampStart = System.currentTimeMillis();

        ScheduledFuture<?> rampTask = scheduler.scheduleAtFixedRate(() -> {

            long elapsedSec = (System.currentTimeMillis() - rampStart) / 1000;
            int expectedUsers = (int) Math.round(usersPerSecond * elapsedSec);

            int current = spawnedUsers.get();
            int toSpawn = expectedUsers - current;

            if (toSpawn <= 0) return;
            if (current >= targetUsers) return;

            int actualSpawn = Math.min(toSpawn, targetUsers - current);

            for (int i = 0; i < actualSpawn; i++) {
                Future<?> f = executor.submit(
                        new VirtualUserRunner(loadTest, allMetrics, stopFlag, SHARED_CLIENT)
                );
                vuFutures.add(f);
            }

            spawnedUsers.addAndGet(actualSpawn);

            log.info("Ramp: spawned {} / {} VUs", spawnedUsers.get(), targetUsers);

        }, 0, 1, TimeUnit.SECONDS);


        // === STOP RAMP-UP AFTER rampSeconds ===
        scheduler.schedule(() -> {
            log.info("Ramp-up finished after {} seconds. Total spawned: {}",
                    rampSeconds, spawnedUsers.get());
            rampTask.cancel(false);
        }, rampSeconds, TimeUnit.SECONDS);


        // === STOP ALL VUs AFTER total duration ===
        scheduler.schedule(() -> {
            log.info("Total test duration {} sec reached → stopping all VUs", durationSeconds);
            stopFlag.set(true);
        }, durationSeconds, TimeUnit.SECONDS);


        // Wait until full duration elapses
        try {
            Thread.sleep(durationSeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Graceful wait for VUs to finish last iteration
        long graceEnd = System.currentTimeMillis() + 3000;
        for (Future<?> f : vuFutures) {
            long remaining = graceEnd - System.currentTimeMillis();
            if (remaining <= 0) break;
            try {
                f.get(remaining, TimeUnit.MILLISECONDS);
            } catch (Exception ignored) {}
        }

        executor.shutdownNow();
        scheduler.shutdownNow();

        long finishedAt = System.currentTimeMillis();
        List<RequestMetrics> collected = new ArrayList<>(allMetrics);

        log.info("Ramp-up test finished: {} metrics collected in {} ms",
                collected.size(), (finishedAt - startedAt));

        return new ExecutionResult(collected, startedAt, finishedAt);
    }
}
