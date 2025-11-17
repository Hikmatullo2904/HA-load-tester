package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkerNodeRepository {

    // clusterId -> list of workers
    private final Map<String, List<WorkerNode>> workerNodesByCluster = new ConcurrentHashMap<>();


    /** Save worker into its cluster **/
    public void saveWorkerNode(WorkerNode node) {
        workerNodesByCluster
                .computeIfAbsent(node.getClusterId(), id -> new ArrayList<>())
                .add(node);
    }


    /** Find all workers for a cluster **/
    public List<WorkerNode> findWorkerByClusterId(String clusterId) {
        return workerNodesByCluster.getOrDefault(clusterId, Collections.emptyList());
    }


    /** Find ALL worker groups **/
    public Map<String, List<WorkerNode>> findAllWorkerGroups() {
        return workerNodesByCluster;
    }


    /** Optional: Find worker by IP **/
    public Optional<WorkerNode> findByIp(String clusterId, String ip) {
        return workerNodesByCluster.getOrDefault(clusterId, Collections.emptyList()).stream()
                .filter(node -> node.getIp().equals(ip))
                .findFirst();
    }



    /** Find worker by workerId across all clusters **/
    public Optional<WorkerNode> findByWorkerId(String workerId) {
        return workerNodesByCluster.values().stream()
                .flatMap(List::stream)
                .filter(node -> node.getId().equals(workerId))
                .findFirst();
    }


    /** Remove worker from repository **/
    public void remove(String workerId) {
        workerNodesByCluster.values().forEach(list ->
                list.removeIf(node -> node.getId().equals(workerId))
        );
    }


    /** Update workerNode: replace old with new **/
    public void update(String workerId, WorkerNode updatedNode) {
        workerNodesByCluster.forEach((clusterId, list) -> {
            ListIterator<WorkerNode> it = list.listIterator();
            while (it.hasNext()) {
                WorkerNode old = it.next();
                if (old.getId().equals(workerId)) {
                    it.set(updatedNode);
                    return; // enough
                }
            }
        });
    }
}
