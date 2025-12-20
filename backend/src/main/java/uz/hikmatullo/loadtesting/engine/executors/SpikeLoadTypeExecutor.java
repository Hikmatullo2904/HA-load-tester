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
public class SpikeLoadTypeExecutor {

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

        int peakVus = loadTest.getProfile().getVirtualUsers();
        int baseVus = Math.max(1, (int) (peakVus * 0.2));
        int duration = loadTest.getProfile().getDurationSeconds();
        int rampUp = loadTest.getProfile().getRampUpSeconds();
        int peakDuration = (int) (duration * 0.2);

        log.info("""
                Running SPIKE test:
                 - base VUs = {}
                 - peak VUs = {}
                 - rampUpSeconds = {}
                 - peakDuration = {}
                 - durationSeconds = {}
                """, baseVus, peakVus, rampUp, peakDuration, duration);

        long startedAt = System.currentTimeMillis();
        Queue<RequestMetrics> allMetrics = new ConcurrentLinkedQueue<>();

        HttpClient SHARED_CLIENT = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        ExecutorService executor = Executors.newThreadPerTaskExecutor(Thread.ofVirtual().factory());
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        AtomicBoolean stopFlag = new AtomicBoolean(false);
        AtomicInteger targetVus = new AtomicInteger(baseVus);

        List<Future<?>> userFutures = new CopyOnWriteArrayList<>();

        // ---------------------------------------------------------
        // Initial VUs (BASE)
        // ---------------------------------------------------------
        scaleUp(baseVus, executor, userFutures, loadTest, allMetrics, stopFlag, SHARED_CLIENT);

        // ---------------------------------------------------------
        // RAMP UP to peak
        // ---------------------------------------------------------
        scheduleRampUp(targetVus, baseVus, peakVus, rampUp, scheduler, () ->
                reconcile(executor, userFutures, targetVus.get(), loadTest, allMetrics, stopFlag, SHARED_CLIENT)
        );

        // ---------------------------------------------------------
        // RAMP DOWN back to base after spike peak window
        // ---------------------------------------------------------
        scheduleRampDown(scheduler, duration - peakDuration, baseVus, targetVus, () ->
                reconcile(executor, userFutures, targetVus.get(), loadTest, allMetrics, stopFlag, SHARED_CLIENT)
        );

        // ---------------------------------------------------------
        // GLOBAL STOP
        // ---------------------------------------------------------
        scheduler.schedule(() -> {
            stopFlag.set(true);
            for (Future<?> f : userFutures) f.cancel(true);
        }, duration, TimeUnit.SECONDS);

        // ---------------------------------------------------------
        // WAIT for all finish
        // ---------------------------------------------------------
        waitAll(userFutures);

        executor.shutdownNow();
        scheduler.shutdownNow();

        long finishedAt = System.currentTimeMillis();
        return new ExecutionResult(new ArrayList<>(allMetrics), startedAt, finishedAt);
    }

    // =========================================================================
    // HELPERS
    // =========================================================================

    private void scheduleRampUp(
            AtomicInteger target, int base, int peak, int rampUpSeconds,
            ScheduledExecutorService scheduler, Runnable reconcile
    ) {
        if (peak <= base || rampUpSeconds <= 0) return;

        int inc = Math.max(1, (peak - base) / rampUpSeconds);

        scheduler.scheduleAtFixedRate(() -> {
            int next = Math.min(peak, target.get() + inc);
            target.set(next);
            reconcile.run();

            if (next >= peak) throw new RuntimeException("STOP_RAMP_UP");
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void scheduleRampDown(
            ScheduledExecutorService scheduler,
            int startAfterSeconds,
            int baseVus,
            AtomicInteger targetVus,
            Runnable reconcile
    ) {
        scheduler.schedule(() -> {
            targetVus.set(baseVus);
            reconcile.run();
        }, startAfterSeconds, TimeUnit.SECONDS);
    }

    private void scaleUp(
            int count,
            ExecutorService executor,
            List<Future<?>> futures,
            LoadTest test,
            Queue<RequestMetrics> metrics,
            AtomicBoolean stop,
            HttpClient client
    ) {
        for (int i = 0; i < count; i++) {
            futures.add(executor.submit(new VirtualUserRunner(test, metrics, stop, client)));
        }
    }

    private void reconcile(
            ExecutorService executor,
            List<Future<?>> futures,
            int desired,
            LoadTest test,
            Queue<RequestMetrics> metrics,
            AtomicBoolean stop,
            HttpClient client
    ) {
        int current = futures.size();

        if (desired > current) {
            scaleUp(desired - current, executor, futures, test, metrics, stop, client);
        } else if (desired < current) {
            int diff = current - desired;
            for (int i = 0; i < diff; i++) {
                Future<?> f = futures.removeLast();
                f.cancel(true);
            }
        }
    }

    private void waitAll(List<Future<?>> futures) {
        for (Future<?> f : futures) {
            try { f.get(); }
            catch (Exception ignored) {}
        }
    }
}
