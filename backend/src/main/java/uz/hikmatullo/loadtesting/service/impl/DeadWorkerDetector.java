package uz.hikmatullo.loadtesting.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import uz.hikmatullo.loadtesting.model.entity.WorkerStatus;
import uz.hikmatullo.loadtesting.model.enums.WorkerStatusEnum;
import uz.hikmatullo.loadtesting.repository.WorkerStatusRepository;
import uz.hikmatullo.loadtesting.repository.WorkerNodeRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Component
public class DeadWorkerDetector {

    private static final Logger log = LoggerFactory.getLogger(DeadWorkerDetector.class);

    private final WorkerStatusRepository statusRepository;
    private final WorkerNodeRepository nodeRepository;

    // Thresholds
    private static final long DISCONNECTED_TIMEOUT = 30;         // seconds
    private static final long DEAD_TIMEOUT = 60 * 20;            // 20 minutes
    private static final long REMOVE_TIMEOUT = 60 * 40;          // 40 minutes

    public DeadWorkerDetector(WorkerStatusRepository statusRepository,
                              WorkerNodeRepository nodeRepository) {
        this.statusRepository = statusRepository;
        this.nodeRepository = nodeRepository;
    }

    @Scheduled(fixedRate = 10_000)
    public void checkWorkers() {
        log.debug("Checking for dead workers");
        Instant now = Instant.now();
        Map<String, WorkerStatus> allStatuses = statusRepository.findAll();

        allStatuses.forEach((workerId, status) -> {
            long delta = Duration.between(status.getLastHeartbeat(), now).toSeconds();

            // 4) Remove permanently
            if (delta > REMOVE_TIMEOUT) {
                log.warn("Worker {} removed from cluster ({} seconds with no heartbeat)", workerId, delta);
                statusRepository.remove(workerId);
                nodeRepository.remove(workerId);
                return;
            }

            Optional<WorkerNode> optionalWorkerNode = nodeRepository.findByWorkerId(workerId);
            if (optionalWorkerNode.isEmpty()) {
                log.info("Node is empty {}", workerId);
                return;
            }
            WorkerNode worker = optionalWorkerNode.get();

            // 3) DEAD
            if (delta > DEAD_TIMEOUT) {
                if (worker.getStatus() != WorkerStatusEnum.DEAD) {
                    worker.setStatus(WorkerStatusEnum.DEAD);

                    log.warn("Worker {} is DEAD ({} seconds since heartbeat)", workerId, delta);
                }
                return;
            }

            // 2) DISCONNECTED
            if (delta > DISCONNECTED_TIMEOUT) {
                if (worker.getStatus() != WorkerStatusEnum.DISCONNECTED) {
                    worker.setStatus(WorkerStatusEnum.DISCONNECTED);

                    log.warn("Worker {} is DISCONNECTED ({} seconds since heartbeat)", workerId, delta);
                }
                return;
            }
        });
    }
}
