package uz.hikmatullo.loadtesting.model.request;

import lombok.Builder;

@Builder
public record HeartbeatRequest(
        String workerId,
        String clusterId,
        double cpuLoad,
        long freeMemory,
        int activeTasks
) {}
