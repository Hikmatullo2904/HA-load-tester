package uz.hikmatullo.loadtesting.service.impl;

import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.exceptions.CustomBadRequestException;
import uz.hikmatullo.loadtesting.exceptions.CustomNotFoundException;
import uz.hikmatullo.loadtesting.model.entity.Cluster;
import uz.hikmatullo.loadtesting.model.request.GroupCreateRequest;
import uz.hikmatullo.loadtesting.model.request.GroupUpdateRequest;
import uz.hikmatullo.loadtesting.model.response.GroupResponse;
import uz.hikmatullo.loadtesting.repository.ClusterRepository;
import uz.hikmatullo.loadtesting.repository.ClusterMembershipRepository;
import uz.hikmatullo.loadtesting.service.interfaces.ClusterService;

import java.util.List;

@Service
public class ClusterServiceImpl implements ClusterService {

    private final ClusterRepository repository;
    private final ClusterMembershipRepository clusterMembershipRepository;

    public ClusterServiceImpl(ClusterRepository repository, ClusterMembershipRepository clusterMembershipRepository) {
        this.repository = repository;
        this.clusterMembershipRepository = clusterMembershipRepository;
    }

    public GroupResponse create(GroupCreateRequest req) {
        validateGroupRequest(req);
        Cluster cluster = new Cluster(req.name(), req.description());
        repository.save(cluster);
        return toResponse(cluster);
    }

    public List<GroupResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public GroupResponse getById(String id) {
        Cluster cluster = repository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Group not found: " + id));
        return toResponse(cluster);
    }

    @Override
    public List<GroupResponse> getConnectedGroups() {
        return clusterMembershipRepository.findAllMasterNodes().stream()
                .map(n -> new GroupResponse(
                        n.getGroupId(), n.getGroupName(), n.getGroupDescription(), null
                ))
                .toList();
    }

    public GroupResponse update(String id, GroupUpdateRequest req) {
        validateGroupUpdateRequest(req);
        Cluster cluster = repository.findById(id)
                .orElseThrow(() -> new CustomNotFoundException("Group not found: " + id));
        cluster.setName(req.name());
        cluster.setDescription(req.description());
        return toResponse(cluster);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    private GroupResponse toResponse(Cluster g) {
        return new GroupResponse(
                g.getId(),
                g.getName(),
                g.getDescription(),
                g.getCreatedAt()
        );
    }

    void validateGroupRequest(GroupCreateRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new CustomBadRequestException("Name cannot be blank");
        }

    }

    void validateGroupUpdateRequest(GroupUpdateRequest req) {
        if (req.name() == null || req.name().isBlank()) {
            throw new CustomBadRequestException("Name cannot be blank");
        }
    }
}