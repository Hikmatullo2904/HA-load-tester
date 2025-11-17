package uz.hikmatullo.loadtesting.mapper;

import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;

public class NodeMapper {
    public static NodeResponse toResponse(WorkerNode node) {
        return new NodeResponse(node.getId(), node.getClusterId(), node.getIp(), node.getConnectedAt());
    }
}
