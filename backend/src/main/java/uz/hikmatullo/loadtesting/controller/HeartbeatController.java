package uz.hikmatullo.loadtesting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void receiveHeartbeat(@RequestBody HeartbeatRequest request) {
        heartbeatService.updateHeartbeat(request);
    }
}
