package uz.hikmatullo.loadtesting.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.exceptions.CustomBadRequestException;
import uz.hikmatullo.loadtesting.exceptions.CustomNotFoundException;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.GroupInfoResponse;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;
import uz.hikmatullo.loadtesting.repository.ClusterRepository;
import uz.hikmatullo.loadtesting.repository.WorkerNodeRepository;
import uz.hikmatullo.loadtesting.service.interfaces.NodeService;
import uz.hikmatullo.loadtesting.util.Util;

import java.util.List;

@Service
public class NodeServiceImpl implements NodeService {

    private static final Logger log = LoggerFactory.getLogger(NodeServiceImpl.class);
    private final WorkerNodeRepository repository;
    private final ClusterRepository clusterRepository;

    public NodeServiceImpl(WorkerNodeRepository repository, ClusterRepository clusterRepository) {
        this.repository = repository;
        this.clusterRepository = clusterRepository;
    }

    @Override
    public GroupInfoResponse addWorkerNode(NodeConnectRequest request) {
        validateConnectRequest(request);
        String host = Util.getCurrentRequestIp();
        log.info("Connection request from host={} for groupId={}", host, request.groupId());

        var group = clusterRepository.findById(request.groupId())
                .orElseThrow(() -> new CustomNotFoundException("Group not found for id " + request.groupId()));

        WorkerNode node = new WorkerNode(request.groupId(), host);
        repository.saveWorkerNode(node);

        log.info("Worker node {} connected successfully to group '{}'", host,  group.getName());
        return new GroupInfoResponse(group.getId(), group.getName(), group.getDescription());
    }

    private void validateConnectRequest(NodeConnectRequest request) {
        if (request.groupId() == null || request.groupId().isBlank()) {
            throw new CustomBadRequestException("Group ID is null");
        }
    }

    @Override
    public List<NodeResponse> getNodesByGroup(String groupId) {
        List<WorkerNode> nodes = repository.findWorkerByGroupId(groupId);

        log.info("Retrieved {} nodes for groupId={}", nodes.size(), groupId);
        return nodes.stream().map(this::toResponse).toList();
    }

    private NodeResponse toResponse(WorkerNode node) {
        return new NodeResponse(node.getId(), node.getGroupId(), node.getHost(), node.getConnectedAt());
    }


}
