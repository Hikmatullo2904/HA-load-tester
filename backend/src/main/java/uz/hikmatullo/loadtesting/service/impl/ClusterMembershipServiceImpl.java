package uz.hikmatullo.loadtesting.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uz.hikmatullo.loadtesting.exceptions.CustomBadRequestException;
import uz.hikmatullo.loadtesting.mapper.ClusterMembershipMapper;
import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;
import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;
import uz.hikmatullo.loadtesting.repository.ClusterMembershipRepository;
import uz.hikmatullo.loadtesting.service.interfaces.ClusterMembershipService;
import uz.hikmatullo.loadtesting.validators.ClusterMembershipValidator;

@Service
public class ClusterMembershipServiceImpl implements ClusterMembershipService {

    private static final Logger log = LoggerFactory.getLogger(ClusterMembershipServiceImpl.class);
    private final RestTemplate restTemplate;
    private final ClusterMembershipRepository clusterMembershipRepository;
    private final ClusterMembershipValidator validator;

    public ClusterMembershipServiceImpl(RestTemplate restTemplate, ClusterMembershipRepository clusterMembershipRepository, ClusterMembershipValidator validator) {
        this.restTemplate = restTemplate;
        this.clusterMembershipRepository = clusterMembershipRepository;
        this.validator = validator;
    }

    @Override
    public void connectToCluster(ClusterMembershipRequest request) {

        validator.validate(request);

        log.info("Attempting to join cluster clusterId={} at {}:{}",
                request.clusterId(), request.ip(), request.port());

        ClusterInfoResponse response = registerWorker(request);

        ClusterMembership membership = ClusterMembershipMapper.toEntity(request, response);

        clusterMembershipRepository.saveMasterNode(membership);

        log.info("Connected to cluster='{}' ({})", response.name(), response.id());
    }

    public ClusterInfoResponse registerWorker(ClusterMembershipRequest request) {

        String url = buildUrl(request);

        NodeConnectRequest connectRequest = new NodeConnectRequest(request.clusterId());

        try {
            ResponseEntity<ClusterInfoResponse> response =
                    restTemplate.postForEntity(url, connectRequest, ClusterInfoResponse.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new CustomBadRequestException("Coordinator returned invalid response");
            }

            return response.getBody();

        } catch (Exception ex) {
            throw new CustomBadRequestException("Unable to connect to coordinator: " + ex.getMessage());
        }
    }

    private String buildUrl(ClusterMembershipRequest request) {
        return "http://" + request.ip() + ":" + request.port() + "/api/v1/nodes/add-worker";
    }


}
