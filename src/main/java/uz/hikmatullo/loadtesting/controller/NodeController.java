package uz.hikmatullo.loadtesting.controller;

import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;
import uz.hikmatullo.loadtesting.service.interfaces.NodeService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeController {

    private static final Logger log = LoggerFactory.getLogger(NodeController.class);
    private final NodeService service;

    public NodeController(NodeService service) {
        this.service = service;
    }

    @PostMapping("/connect")
    @ResponseStatus(HttpStatus.CREATED)
    public NodeResponse connect(@RequestBody NodeConnectRequest request) {
        log.info("Received connection from node: host={} groupId={}", request.host(), request.groupId());
        return service.connect(request);
    }

    @GetMapping("/group/{groupId}")
    public List<NodeResponse> getByGroup(@PathVariable String groupId) {
        log.info("Fetching connected nodes for groupId={}", groupId);
        return service.getNodesByGroup(groupId);
    }
}
