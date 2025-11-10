package uz.hikmatullo.loadtesting.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.hikmatullo.loadtesting.model.entity.MasterNode;
import uz.hikmatullo.loadtesting.model.request.JoinMasterNodeGroupRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.GroupInfoResponse;
import uz.hikmatullo.loadtesting.repository.MasterNodeRepository;
import uz.hikmatullo.loadtesting.service.interfaces.ConnectToMasterNodeService;

@Service
public class ConnectToMasterNodeServiceImpl implements ConnectToMasterNodeService {

    private static final Logger log = LoggerFactory.getLogger(ConnectToMasterNodeServiceImpl.class);
    private final RestTemplate restTemplate;
    private final MasterNodeRepository masterNodeRepository;

    public ConnectToMasterNodeServiceImpl(RestTemplate restTemplate, MasterNodeRepository masterNodeRepository) {
        this.restTemplate = restTemplate;
        this.masterNodeRepository = masterNodeRepository;
    }

    @Override
    public void connectToMaster(JoinMasterNodeGroupRequest request) {
        try {
            String url = "http://" + request.ip() + ":" + request.port() + "/api/nodes/connect";
            log.info("Connecting to master={} groupId={} ...", url, request.groupId());

            NodeConnectRequest req = new NodeConnectRequest(request.groupId());
            HttpEntity<NodeConnectRequest> entity = new HttpEntity<>(req);

            ResponseEntity<GroupInfoResponse> groupInfoResponseResponseEntity = restTemplate.postForEntity(url, entity, GroupInfoResponse.class);

            if (groupInfoResponseResponseEntity.getStatusCode().is2xxSuccessful()) {
                GroupInfoResponse groupInfo = groupInfoResponseResponseEntity.getBody();
                if (groupInfo == null) {
                    log.error("Failed to connect to master. Group info is null");
                    throw new RuntimeException("Failed to connect to master. Group info is null");
                }
                MasterNode masterNode = new MasterNode(request.ip(), request.port(), groupInfo.groupId(), groupInfo.groupName());
                masterNodeRepository.saveMasterNode(masterNode);

                log.info("Successfully connected to master group='{}' ({})", groupInfo.groupName(), groupInfo.groupId());
            } else {
                log.error("Failed to connect to master. HTTP status: {} body: {}", groupInfoResponseResponseEntity.getStatusCode(), groupInfoResponseResponseEntity.getBody());
            }



        } catch (Exception e) {
            log.error("Connection to master failed: {}", e.getMessage(), e);
        }
    }

    private void validateRequest(JoinMasterNodeGroupRequest request) {
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
