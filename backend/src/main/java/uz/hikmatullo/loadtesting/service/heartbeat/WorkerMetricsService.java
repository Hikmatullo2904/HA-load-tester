package uz.hikmatullo.loadtesting.service.heartbeat;

import org.springframework.stereotype.Component;

@Component
public class WorkerMetricsService {

    public double cpuLoad() {
        // for now, fake data. We will implement it later when we start using metrics
        return 0.25;
    }

    public long freeMemory() {
        // for now, fake data. We will implement it later when we start using metrics
        return Runtime.getRuntime().freeMemory();
    }

    public int activeTasks() {
        // for now, fake data. We will implement it later when we start using metrics
        return 0;
    }
}
