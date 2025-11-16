package uz.hikmatullo.loadtesting.controller;

import org.springframework.web.bind.annotation.*;
import uz.hikmatullo.loadtesting.model.request.GroupCreateRequest;
import uz.hikmatullo.loadtesting.model.request.GroupUpdateRequest;
import uz.hikmatullo.loadtesting.model.response.GroupResponse;
import uz.hikmatullo.loadtesting.service.interfaces.ClusterService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class ClusterController {

    private final ClusterService clusterService;

    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }


    @PostMapping
    public GroupResponse create(@RequestBody GroupCreateRequest req) {
        return clusterService.create(req);
    }

    @GetMapping
    public List<GroupResponse> getAll() {
        return clusterService.getAll();
    }

    @GetMapping("/{id}")
    public GroupResponse getById(@PathVariable String id) {
        return clusterService.getById(id);
    }

    @GetMapping("/connected")
    public List<GroupResponse> getMyGroups() {
        return clusterService.getConnectedGroups();
    }

    @PutMapping("/{id}")
    public GroupResponse update(@PathVariable String id, @RequestBody GroupUpdateRequest req) {
        return clusterService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        clusterService.delete(id);
    }


}
