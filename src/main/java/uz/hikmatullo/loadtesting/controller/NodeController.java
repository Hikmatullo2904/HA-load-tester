package uz.hikmatullo.loadtesting.controller;

import uz.hikmatullo.loadtesting.model.request.JoinMasterNodeGroupRequest;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.GroupInfoResponse;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;
import uz.hikmatullo.loadtesting.service.interfaces.ConnectToMasterNodeService;
import uz.hikmatullo.loadtesting.service.interfaces.NodeService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeController {
    private final NodeService service;
    private final ConnectToMasterNodeService connectToMasterNodeService;

    public NodeController(NodeService service, ConnectToMasterNodeService connectToMasterNodeService) {
        this.service = service;
        this.connectToMasterNodeService = connectToMasterNodeService;
    }

    /*
    * This method accepts connection request from worker nodes
    * */
    @PostMapping("/receive-join-group")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupInfoResponse connect(@RequestBody NodeConnectRequest request) {
        return service.receiveConnection(request);
    }

    @GetMapping("/group/{groupId}")
    public List<NodeResponse> getByGroup(@PathVariable String groupId) {
        return service.getNodesByGroup(groupId);
    }


    @PostMapping("/send-join-group")
    public void connectToMaster(@RequestBody JoinMasterNodeGroupRequest request) {
        connectToMasterNodeService.connectToMaster(request);
    }
}
