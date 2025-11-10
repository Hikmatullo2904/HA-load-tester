package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
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
