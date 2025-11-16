package uz.hikmatullo.loadtesting.controller;

import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;
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
    public ClusterInfoResponse connect(@RequestBody NodeConnectRequest request) {
        return service.addWorkerNode(request);
    }


    @GetMapping("/cluster/{clusterId}")
    public List<NodeResponse> getByGroup(@PathVariable String clusterId) {
        return service.getNodesByGroup(clusterId);
    }


    @PostMapping("/join-cluster")
    public void connectToMaster(@RequestBody ClusterMembershipRequest request) {
        clusterMembershipService.connectToMaster(request);
    }


}
