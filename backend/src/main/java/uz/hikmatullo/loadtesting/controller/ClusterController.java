package uz.hikmatullo.loadtesting.controller;

import org.springframework.web.bind.annotation.*;
import uz.hikmatullo.loadtesting.model.request.ClusterCreateRequest;
import uz.hikmatullo.loadtesting.model.request.ClusterUpdateRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterResponse;
import uz.hikmatullo.loadtesting.service.interfaces.ClusterService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clusters")
public class ClusterController {

    private final ClusterService clusterService;

    public ClusterController(ClusterService clusterService) {
        this.clusterService = clusterService;
    }


    @PostMapping
    public ClusterResponse create(@RequestBody ClusterCreateRequest req) {
        return clusterService.create(req);
    }

    @GetMapping
    public List<ClusterResponse> getAll() {
        return clusterService.getAll();
    }

    @GetMapping("/{id}")
    public ClusterResponse getById(@PathVariable String id) {
        return clusterService.getById(id);
    }

    @GetMapping("/connected")
    public List<ClusterResponse> getMyGroups() {
        return clusterService.getConnectedGroups();
    }

    @PutMapping("/{id}")
    public ClusterResponse update(@PathVariable String id, @RequestBody ClusterUpdateRequest req) {
        return clusterService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        clusterService.delete(id);
    }


}
