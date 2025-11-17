package uz.hikmatullo.loadtesting.model.response;

import lombok.Builder;
import uz.hikmatullo.loadtesting.model.enums.WorkerStatusEnum;

import java.time.Instant;

@Builder
public record WorkerNodeResponse(
        String id,
        String clusterId,
        String ip,
        Instant connectedAt,
        WorkerStatusEnum status
) {}
