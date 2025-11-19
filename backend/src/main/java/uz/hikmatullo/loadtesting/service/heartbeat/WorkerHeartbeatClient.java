package uz.hikmatullo.loadtesting.service.heartbeat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;
import uz.hikmatullo.loadtesting.model.request.HeartbeatRequest;

@Component
public class WorkerHeartbeatClient {

    private final Logger log = LoggerFactory.getLogger(WorkerHeartbeatClient.class);

    private final RestTemplate restTemplate;

    public WorkerHeartbeatClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void sendHeartbeat(ClusterMembership membership, HeartbeatRequest req) {

        String url = "http://" + membership.getIp() + ":" + membership.getPort() + "/api/v1/heartbeat";

        HttpEntity<HeartbeatRequest> entity = new HttpEntity<>(req);

        try {
            restTemplate.postForEntity(url, entity, Void.class);
        } catch (Exception ex) {
            // Coordinator might be temporarily unreachable
            log.error("Heartbeat send failed: {}", ex.getMessage());
        }
    }
}
