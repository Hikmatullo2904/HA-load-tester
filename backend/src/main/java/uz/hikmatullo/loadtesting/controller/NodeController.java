package uz.hikmatullo.loadtesting.controller;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.*;
import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;
import uz.hikmatullo.loadtesting.model.response.WorkerNodeResponse;
import uz.hikmatullo.loadtesting.service.interfaces.ClusterMembershipService;
import uz.hikmatullo.loadtesting.service.interfaces.NodeService;

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
    @Hidden
    @PostMapping("/add-worker")
    public ClusterInfoResponse connect(@RequestBody NodeConnectRequest request) {
        return service.addWorkerNode(request);
    }


    @GetMapping("/cluster/{clusterId}")
    public List<WorkerNodeResponse> getByGroup(@PathVariable String clusterId) {
        return service.getNodesByCluster(clusterId);
    }


    @PostMapping("/join-cluster")
    public void connectToMaster(@RequestBody ClusterMembershipRequest request) {
        clusterMembershipService.connectToCluster(request);
    }


}
