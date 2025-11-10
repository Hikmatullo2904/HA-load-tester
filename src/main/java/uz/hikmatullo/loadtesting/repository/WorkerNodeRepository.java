package uz.hikmatullo.loadtesting.repository;

import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class WorkerNodeRepository {
    private final Map<String, List<WorkerNode>> worderNodesByGroup = new ConcurrentHashMap<>();


    public void saveWorkerNode(WorkerNode node) {
        worderNodesByGroup.computeIfAbsent(node.getGroupId(), k -> new ArrayList<>()).add(node);
    }

    public List<WorkerNode> findWorkerByGroupId(String groupId) {
        return worderNodesByGroup.getOrDefault(groupId, Collections.emptyList());
    }

    public Map<String, List<WorkerNode>> findAllWorkerGroups() {
        return worderNodesByGroup;
    }
}
