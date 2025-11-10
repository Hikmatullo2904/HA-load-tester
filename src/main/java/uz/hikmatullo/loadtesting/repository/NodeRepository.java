package uz.hikmatullo.loadtesting.repository;

import uz.hikmatullo.loadtesting.model.entity.MasterNode;
import uz.hikmatullo.loadtesting.model.entity.NodeInfo;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NodeRepository {
    private final Map<String, List<NodeInfo>> worderNodesByGroup = new ConcurrentHashMap<>();
    private final Map<String, MasterNode> masterNodes = new ConcurrentHashMap<>();

    public void saveWorkerNode(NodeInfo node) {
        worderNodesByGroup.computeIfAbsent(node.getGroupId(), k -> new ArrayList<>()).add(node);
    }

    public List<NodeInfo> findWorkerByGroupId(String groupId) {
        return worderNodesByGroup.getOrDefault(groupId, Collections.emptyList());
    }

    public Map<String, List<NodeInfo>> findAllWorkerGroups() {
        return worderNodesByGroup;
    }

    public void saveMasterNode(MasterNode node) {
        masterNodes.put(node.getGroupId(), node);
    }

    public Optional<MasterNode> findMasterByGroupId(String groupId) {
        return Optional.ofNullable(masterNodes.get(groupId));
    }
}
