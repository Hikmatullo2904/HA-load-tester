package uz.hikmatullo.loadtesting.service;

import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.exceptions.NotFoundException;
import uz.hikmatullo.loadtesting.model.entity.Group;
import uz.hikmatullo.loadtesting.model.request.GroupCreateRequest;
import uz.hikmatullo.loadtesting.model.request.GroupUpdateRequest;
import uz.hikmatullo.loadtesting.model.response.GroupResponse;
import uz.hikmatullo.loadtesting.repository.GroupRepository;

import java.util.List;

@Service
public class GroupService {

    private final GroupRepository repository;

    public GroupService(GroupRepository repository) {
        this.repository = repository;
    }

    public GroupResponse create(GroupCreateRequest req) {
        Group group = new Group(req.name(), req.description(), req.masterHost());
        repository.save(group);
        return toResponse(group);
    }

    public List<GroupResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public GroupResponse getById(String id) {
        Group group = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group not found: " + id));
        return toResponse(group);
    }

    public GroupResponse update(String id, GroupUpdateRequest req) {
        Group group = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Group not found: " + id));
        group.setName(req.name());
        group.setDescription(req.description());
        return toResponse(group);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    private GroupResponse toResponse(Group g) {
        return new GroupResponse(
                g.getId(),
                g.getName(),
                g.getDescription(),
                g.getMasterHost(),
                g.getCreatedAt()
        );
    }
}