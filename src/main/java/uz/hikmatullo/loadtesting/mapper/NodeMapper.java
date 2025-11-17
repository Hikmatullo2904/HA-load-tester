package uz.hikmatullo.loadtesting.mapper;

import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import uz.hikmatullo.loadtesting.model.response.WorkerNodeResponse;

public class NodeMapper {
    public static WorkerNodeResponse toResponse(WorkerNode node) {
        return WorkerNodeResponse.builder()
                .id(node.getId())
                .clusterId(node.getClusterId())
                .ip(node.getIp())
                .connectedAt(node.getConnectedAt())
                .status(node.getStatus())
                .build();
    }
}
