package uz.hikmatullo.loadtesting.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.hikmatullo.loadtesting.model.request.HeartbeatRequest;
import uz.hikmatullo.loadtesting.service.impl.HeartbeatService;

@RestController
@RequestMapping("/api/v1/heartbeat")
public class HeartbeatController {

    private final HeartbeatService heartbeatService;

    public HeartbeatController(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @PostMapping
    public ResponseEntity<Void> receiveHeartbeat(@RequestBody HeartbeatRequest request) {
        heartbeatService.updateHeartbeat(request);
        return ResponseEntity.ok().build();
    }
}
