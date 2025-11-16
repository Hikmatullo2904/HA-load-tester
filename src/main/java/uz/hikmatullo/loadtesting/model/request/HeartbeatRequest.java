package uz.hikmatullo.loadtesting.model.request;

public record HeartbeatRequest(
        String clusterId,
        double cpuLoad,
        long freeMemory,
        int activeTasks
) {}
