package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import uz.hikmatullo.loadtesting.model.entity.MasterNode;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class MasterNodeRepository {
    private final Map<String, MasterNode> masterNodes = new ConcurrentHashMap<>();

    public void saveMasterNode(MasterNode node) {
        masterNodes.put(node.getGroupId(), node);
    }

    public Optional<MasterNode> findMasterByGroupId(String groupId) {
        return Optional.ofNullable(masterNodes.get(groupId));
    }
}
