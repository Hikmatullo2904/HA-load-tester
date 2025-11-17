package uz.hikmatullo.loadtesting.model.entity;

import lombok.Getter;
import lombok.Setter;
import uz.hikmatullo.loadtesting.model.enums.WorkerStatusEnum;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class WorkerNode {
    private String id = UUID.randomUUID().toString();
    private String clusterId;
    private String ip;
    private Instant connectedAt = Instant.now();
    private WorkerStatusEnum status;

    public WorkerNode(String clusterId, String ip) {
        this.clusterId = clusterId;
        this.ip = ip;
        this.status = WorkerStatusEnum.ALIVE;
    }

}
