package uz.hikmatullo.loadtesting.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.WorkerStatus;
import uz.hikmatullo.loadtesting.repository.WorkerStatusRepository;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Component
public class DeadWorkerDetector {

    private static final Logger log = LoggerFactory.getLogger(DeadWorkerDetector.class);
    private final WorkerStatusRepository workerStatusRepository;

    private static final long DISCONNECTED_TIMEOUT_SECONDS = 30;
    private static final long DEAD_TIMEOUT_SECONDS = 60 * 20;

    private static final long REMOVE_WORKER_TIMEOUT = 60 * 40;

    public DeadWorkerDetector(WorkerStatusRepository workerStatusRepository) {
        this.workerStatusRepository = workerStatusRepository;
    }

    @Scheduled(fixedRate = 10000)
    public void checkWorkers() {
        Instant now = Instant.now();
        Map<String, WorkerStatus> statuses = workerStatusRepository.findAll();

        statuses.forEach((clusterID, status) -> {
            long delta = Duration.between(status.getLastHeartbeat(), now).toSeconds();
            if(delta > REMOVE_WORKER_TIMEOUT) {


            } else if (delta > DEAD_TIMEOUT_SECONDS) {

            } else if (delta > DISCONNECTED_TIMEOUT_SECONDS) {
                log.warn("Worker {} is Disconnected. Last heartbeat was {} seconds ago", clusterID, delta);


            }
        });
    }
}
