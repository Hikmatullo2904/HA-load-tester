package uz.hikmatullo.loadtesting.model.entity;

import java.time.Instant;
import java.util.UUID;

public class WorkerNode {
    private final String id = UUID.randomUUID().toString();
    private final String clusterId;
    private final String host;
    private final Instant connectedAt = Instant.now();

    public WorkerNode(String clusterId, String host) {
        this.clusterId = clusterId;
        this.host = host;
    }

    public String getId() { return id; }
    public String getClusterId() { return clusterId; }
    public String getHost() { return host; }
    public Instant getConnectedAt() { return connectedAt; }
}
