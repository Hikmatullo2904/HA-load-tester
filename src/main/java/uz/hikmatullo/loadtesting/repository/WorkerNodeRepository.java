package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkerNodeRepository {
    private final Map<String, List<WorkerNode>> worderNodesByGroup = new ConcurrentHashMap<>();


    public void saveWorkerNode(WorkerNode node) {
        worderNodesByGroup.computeIfAbsent(node.getClusterId(), k -> new ArrayList<>()).add(node);
    }

    public List<WorkerNode> findWorkerByGroupId(String groupId) {
        return worderNodesByGroup.getOrDefault(groupId, Collections.emptyList());
    }

    public Map<String, List<WorkerNode>> findAllWorkerGroups() {
        return worderNodesByGroup;
    }

    public Optional<WorkerNode> findByHost(String host) {
        return worderNodesByGroup.values().stream()
                .flatMap(List::stream)
                .filter(node -> node.getHost().equals(host))
                .findFirst();
    }
}
