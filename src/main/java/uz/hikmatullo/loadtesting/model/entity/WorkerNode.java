package uz.hikmatullo.loadtesting.model.entity;

import java.time.Instant;
import java.util.UUID;

public class WorkerNode {
    private final String id = UUID.randomUUID().toString();
    private final String groupId;
    private final String host;
    private final Instant connectedAt = Instant.now();

    public WorkerNode(String groupId, String host) {
        this.groupId = groupId;
        this.host = host;
    }

    public String getId() { return id; }
    public String getGroupId() { return groupId; }
    public String getHost() { return host; }
    public Instant getConnectedAt() { return connectedAt; }
}
