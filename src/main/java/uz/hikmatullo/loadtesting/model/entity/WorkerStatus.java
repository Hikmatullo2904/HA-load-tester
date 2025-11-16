package uz.hikmatullo.loadtesting.model.entity;

import java.time.Instant;

public class WorkerStatus {

    private Instant lastHeartbeat;
    private double cpuLoad;
    private long freeMemory;
    private int activeTasks;

    public WorkerStatus(Instant lastHeartbeat, double cpuLoad, long freeMemory, int activeTasks) {
        this.lastHeartbeat = lastHeartbeat;
        this.cpuLoad = cpuLoad;
        this.freeMemory = freeMemory;
        this.activeTasks = activeTasks;
    }

    public Instant lastHeartbeat() { return lastHeartbeat; }
    public double cpuLoad() { return cpuLoad; }
    public long freeMemory() { return freeMemory; }
    public int activeTasks() { return activeTasks; }
}
