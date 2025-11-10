package uz.hikmatullo.loadtesting.controller;

import org.springframework.web.bind.annotation.*;
import uz.hikmatullo.loadtesting.model.request.GroupCreateRequest;
import uz.hikmatullo.loadtesting.model.request.GroupUpdateRequest;
import uz.hikmatullo.loadtesting.model.response.GroupResponse;
import uz.hikmatullo.loadtesting.service.interfaces.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }


    @PostMapping
    public GroupResponse create(@RequestBody GroupCreateRequest req) {
        return groupService.create(req);
    }

    @GetMapping
    public List<GroupResponse> getAll() {
        return groupService.getAll();
    }

    @GetMapping("/{id}")
    public GroupResponse getById(@PathVariable String id) {
        return groupService.getById(id);
    }

    @PutMapping("/{id}")
    public GroupResponse update(@PathVariable String id, @RequestBody GroupUpdateRequest req) {
        return groupService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        groupService.delete(id);
    }


}
