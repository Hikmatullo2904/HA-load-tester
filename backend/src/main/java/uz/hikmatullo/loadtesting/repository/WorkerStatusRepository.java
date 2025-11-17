package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.WorkerStatus;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkerStatusRepository {

    /**
     * This map stores worker statuses for each worker node
     * Map<workerId, WorkerStatus>
     */
    private static final Map<String, WorkerStatus> workerStatus = new ConcurrentHashMap<>();

    public void save(String workerId, WorkerStatus status) {
        workerStatus.put(workerId, status);
    }

    public WorkerStatus findByWorkerId(String clusterId) {
        return workerStatus.get(clusterId);
    }

    public Map<String, WorkerStatus> findAll() {
        return workerStatus;
    }

    public void remove(String workerId) {
        workerStatus.remove(workerId);
    }

}
