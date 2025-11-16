package uz.hikmatullo.loadtesting.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.exceptions.CustomNotFoundException;
import uz.hikmatullo.loadtesting.mapper.NodeMapper;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;
import uz.hikmatullo.loadtesting.repository.ClusterRepository;
import uz.hikmatullo.loadtesting.repository.WorkerNodeRepository;
import uz.hikmatullo.loadtesting.service.interfaces.NodeService;
import uz.hikmatullo.loadtesting.util.Util;
import uz.hikmatullo.loadtesting.validators.NodeValidator;

import java.util.List;
import java.util.Optional;

@Service
public class NodeServiceImpl implements NodeService {

    private static final Logger log = LoggerFactory.getLogger(NodeServiceImpl.class);
    private final WorkerNodeRepository repository;
    private final ClusterRepository clusterRepository;
    private final NodeValidator nodeValidator;

    public NodeServiceImpl(WorkerNodeRepository repository, ClusterRepository clusterRepository, NodeValidator nodeValidator) {
        this.repository = repository;
        this.clusterRepository = clusterRepository;
        this.nodeValidator = nodeValidator;
    }

    @Override
    public ClusterInfoResponse addWorkerNode(NodeConnectRequest request) {
        nodeValidator.validateConnectRequest(request);

        String host = Util.getCurrentRequestIp();
        log.info("Connection request from host={} for clusterId={}", host, request.clusterId());

        //Checking if the host is already connected to a group
        Optional<WorkerNode> existingNode = repository.findByHost(host);
        if (existingNode.isPresent()) {
            log.info("Host {} is already connected to group '{}'", host, existingNode.get().getClusterId());
            return new ClusterInfoResponse(existingNode.get().getClusterId(), existingNode.get().getClusterId(), existingNode.get().getClusterId());
        }

        var group = clusterRepository.findById(request.clusterId())
                .orElseThrow(() -> new CustomNotFoundException("Group not found for id " + request.clusterId()));

        WorkerNode node = new WorkerNode(request.clusterId(), host);
        repository.saveWorkerNode(node);

        log.info("Worker node {} connected successfully to group '{}'", host,  group.getName());
        return new ClusterInfoResponse(group.getId(), group.getName(), group.getDescription());
    }

    @Override
    public List<NodeResponse> getNodesByGroup(String groupId) {
        List<WorkerNode> nodes = repository.findWorkerByGroupId(groupId);

        log.info("Retrieved {} nodes for clusterId={}", nodes.size(), groupId);
        return nodes.stream().map(NodeMapper::toResponse).toList();
    }



}
