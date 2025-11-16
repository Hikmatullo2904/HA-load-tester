package uz.hikmatullo.loadtesting.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.hikmatullo.loadtesting.exceptions.CustomBadRequestException;
import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;
import uz.hikmatullo.loadtesting.model.request.GroupMembershipRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.GroupInfoResponse;
import uz.hikmatullo.loadtesting.repository.ClusterMembershipRepository;
import uz.hikmatullo.loadtesting.service.interfaces.ClusterMembershipService;

@Service
public class ClusterMembershipServiceImpl implements ClusterMembershipService {

    private static final Logger log = LoggerFactory.getLogger(ClusterMembershipServiceImpl.class);
    private final RestTemplate restTemplate;
    private final ClusterMembershipRepository clusterMembershipRepository;

    public ClusterMembershipServiceImpl(RestTemplate restTemplate, ClusterMembershipRepository clusterMembershipRepository) {
        this.restTemplate = restTemplate;
        this.clusterMembershipRepository = clusterMembershipRepository;
    }

    @Override
    public void connectToMaster(GroupMembershipRequest request) {

        validateRequest(request);

        String url = "http://" + request.ip() + ":" + request.port() + "/api/v1/nodes/add-worker";
        log.info("Connecting to master={} groupId={} ...", url, request.groupId());

        NodeConnectRequest req = new NodeConnectRequest(request.groupId());
        HttpEntity<NodeConnectRequest> entity = new HttpEntity<>(req);

        try {
            ResponseEntity<GroupInfoResponse> groupInfoResponseResponseEntity = restTemplate.postForEntity(url, entity, GroupInfoResponse.class);

            if (groupInfoResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                GroupInfoResponse groupInfo = groupInfoResponseResponseEntity.getBody();
                if (groupInfo == null) {
                    log.error("Failed to connect to master. Group info is null");
                    throw new RuntimeException("Failed to connect to master. Group info is null");
                }
                ClusterMembership clusterMembership = new ClusterMembership(request.ip(), request.port(), groupInfo.id(), groupInfo.name(), groupInfo.description());
                clusterMembershipRepository.saveMasterNode(clusterMembership);

                log.info("Successfully connected to master group='{}' ({})", groupInfo.name(), groupInfo.id());
            } else {
                log.error("Failed to connect to master. HTTP status: {} body: {}", groupInfoResponseResponseEntity.getStatusCode(), groupInfoResponseResponseEntity.getBody());
                throw new CustomBadRequestException("Failed to connect master");
            }
        }catch (Exception e) {
            throw new CustomBadRequestException(e.getMessage());
        }
    }

    private void validateRequest(GroupMembershipRequest request) {
        if (request.ip() == null || request.ip().isEmpty()) {
            throw new IllegalArgumentException("IP address is required");
        }
        if (request.port() <= 0) {
            throw new IllegalArgumentException("Port must be greater than 0");
        }
        if (request.groupId() == null || request.groupId().isEmpty()) {
            throw new IllegalArgumentException("Group ID is required");
        }
    }


}
