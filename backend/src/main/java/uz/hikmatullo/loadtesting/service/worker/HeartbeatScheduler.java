package uz.hikmatullo.loadtesting.service.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;
import uz.hikmatullo.loadtesting.model.request.HeartbeatRequest;
import uz.hikmatullo.loadtesting.repository.ClusterMembershipRepository;

import java.util.List;


@Component
public class HeartbeatScheduler {

    private static final Logger log = LoggerFactory.getLogger(HeartbeatScheduler.class);

    private final WorkerMetricsService metricsService;
    private final WorkerHeartbeatClient heartbeatClient;
    private final ClusterMembershipRepository clusterMembershipRepository;

    public HeartbeatScheduler(
            WorkerMetricsService metricsService,
            WorkerHeartbeatClient heartbeatClient,
            ClusterMembershipRepository clusterMembershipRepository
    ) {
        this.metricsService = metricsService;
        this.heartbeatClient = heartbeatClient;
        this.clusterMembershipRepository = clusterMembershipRepository;
    }

    @Scheduled(fixedRate = 5000)
    public void sendHeartbeat() {
        log.debug("Sending heartbeat to all master nodes started");
        List<ClusterMembership> allMasterNodes = clusterMembershipRepository.findAllMasterNodes();

        log.debug("Found {} master nodes", allMasterNodes.size());
        for (ClusterMembership membership : allMasterNodes) {

            HeartbeatRequest req = HeartbeatRequest.builder()
                    .workerId(membership.getGivenWorkerId())
                    .clusterId(membership.getClusterId())
                    .cpuLoad(metricsService.cpuLoad())
                    .freeMemory(metricsService.freeMemory())
                    .activeTasks(metricsService.activeTasks())
                    .build();

            heartbeatClient.sendHeartbeat(membership, req);
        }
    }
}
