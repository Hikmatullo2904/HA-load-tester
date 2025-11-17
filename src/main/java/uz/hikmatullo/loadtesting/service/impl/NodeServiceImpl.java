package uz.hikmatullo.loadtesting.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.exceptions.CustomNotFoundException;
import uz.hikmatullo.loadtesting.mapper.NodeMapper;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import uz.hikmatullo.loadtesting.model.request.HeartbeatRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;
import uz.hikmatullo.loadtesting.model.response.WorkerNodeResponse;
import uz.hikmatullo.loadtesting.repository.ClusterRepository;
import uz.hikmatullo.loadtesting.repository.WorkerNodeRepository;
import uz.hikmatullo.loadtesting.service.interfaces.NodeService;
import uz.hikmatullo.loadtesting.util.Util;
import uz.hikmatullo.loadtesting.validators.NodeValidator;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NodeServiceImpl implements NodeService {

    private static final Logger log = LoggerFactory.getLogger(NodeServiceImpl.class);
    private final WorkerNodeRepository repository;
    private final ClusterRepository clusterRepository;
    private final NodeValidator nodeValidator;
    private final HeartbeatService heartbeatService;



    @Override
    public ClusterInfoResponse addWorkerNode(NodeConnectRequest request) {
        nodeValidator.validateConnectRequest(request);

        String currentRequestIp = Util.getCurrentRequestIp();
        log.info("Connection request from ip={} for clusterId={}", currentRequestIp, request.clusterId());

        //Checking if the ip is already connected to a group
        Optional<WorkerNode> existingNode = repository.findByIp(request.clusterId(), currentRequestIp);
        if (existingNode.isPresent()) {
            log.info("Host {} is already connected to group '{}'", currentRequestIp, existingNode.get().getClusterId());
            return new ClusterInfoResponse(existingNode.get().getClusterId(),
                    existingNode.get().getClusterId(),
                    existingNode.get().getClusterId(),
                    existingNode.get().getId());
        }

        var cluster = clusterRepository.findById(request.clusterId())
                .orElseThrow(() -> new CustomNotFoundException("Cluster not found for id " + request.clusterId()));

        WorkerNode node = new WorkerNode(request.clusterId(), currentRequestIp);
        repository.saveWorkerNode(node);
        createFirstHeartBeat(request, node);

        log.info("Worker node {} connected successfully to group '{}'", currentRequestIp,  cluster.getName());
        return new ClusterInfoResponse(cluster.getId(), cluster.getName(), cluster.getDescription(), node.getId());
    }

    private void createFirstHeartBeat(NodeConnectRequest request, WorkerNode node) {
        heartbeatService.createHeartbeat(HeartbeatRequest.builder()
                        .clusterId(request.clusterId())
                        .workerId(node.getId())
                        .cpuLoad(0)
                        .freeMemory(0)
                        .activeTasks(0)
                        .build());
    }

    @Override
    public List<WorkerNodeResponse> getNodesByCluster(String groupId) {
        List<WorkerNode> nodes = repository.findWorkerByClusterId(groupId);

        log.debug("Retrieved {} nodes for clusterId={}", nodes.size(), groupId);
        return nodes.stream().map(NodeMapper::toResponse).toList();
    }



}
