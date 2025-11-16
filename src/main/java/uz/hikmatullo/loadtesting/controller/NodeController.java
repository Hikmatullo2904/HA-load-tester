package uz.hikmatullo.loadtesting.controller;

import uz.hikmatullo.loadtesting.model.request.GroupMembershipRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.GroupInfoResponse;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;
import uz.hikmatullo.loadtesting.service.interfaces.ClusterMembershipService;
import uz.hikmatullo.loadtesting.service.interfaces.NodeService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeController {
    private final NodeService service;
    private final ClusterMembershipService clusterMembershipService;

    public NodeController(NodeService service, ClusterMembershipService clusterMembershipService) {
        this.service = service;
        this.clusterMembershipService = clusterMembershipService;
    }

    /*
    * This method accepts connection request from worker nodes
    * */
    @PostMapping("/add-worker")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupInfoResponse connect(@RequestBody NodeConnectRequest request) {
        return service.addWorkerNode(request);
    }


    @GetMapping("/group/{groupId}")
    public List<NodeResponse> getByGroup(@PathVariable String groupId) {
        return service.getNodesByGroup(groupId);
    }


    @PostMapping("/join-master")
    public void connectToMaster(@RequestBody GroupMembershipRequest request) {
        clusterMembershipService.connectToMaster(request);
    }


}
