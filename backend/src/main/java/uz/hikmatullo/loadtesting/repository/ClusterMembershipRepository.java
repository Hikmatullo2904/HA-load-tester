package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClusterMembershipRepository {
    private final Map<String, ClusterMembership> masterNodes = new ConcurrentHashMap<>();

    public void saveMasterNode(ClusterMembership node) {
        masterNodes.put(node.getClusterId(), node);
    }

    public Optional<ClusterMembership> findMasterByGroupId(String groupId) {
        return Optional.ofNullable(masterNodes.get(groupId));
    }

    public List<ClusterMembership> findAllMasterNodes() {
        return new ArrayList<>(masterNodes.values());
    }
}
