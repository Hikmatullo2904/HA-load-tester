package uz.hikmatullo.loadtesting.service.impl;

import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.model.entity.WorkerStatus;
import uz.hikmatullo.loadtesting.model.request.HeartbeatRequest;
import uz.hikmatullo.loadtesting.repository.WorkerStatusRepository;

import java.time.Instant;
import java.util.Map;

@Service
public class HeartbeatService {

    private final WorkerStatusRepository workerStatusRepository;

    public HeartbeatService(WorkerStatusRepository workerStatusRepository) {
        this.workerStatusRepository = workerStatusRepository;
    }

    public void updateHeartbeat(HeartbeatRequest req) {
        workerStatusRepository.save(req.clusterId(),
                WorkerStatus.builder()
                        .lastHeartbeat(Instant.now())
                        .cpuLoad(req.cpuLoad())
                        .freeMemory(req.freeMemory())
                        .activeTasks(req.activeTasks())
                        .build()
        );
    }

    public Map<String, WorkerStatus> getWorkerStatuses() {
        return workerStatusRepository.findAll();
    }
}
