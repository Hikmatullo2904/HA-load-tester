package uz.hikmatullo.loadtesting.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.hikmatullo.loadtesting.model.request.LoadTestRequest;
import uz.hikmatullo.loadtesting.model.response.LoadTestResponse;
import uz.hikmatullo.loadtesting.service.interfaces.LoadTestService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/load-tests")
@RequiredArgsConstructor
public class LoadTestController {

    private final LoadTestService service;

    @PostMapping
    public ResponseEntity<LoadTestResponse> create(@RequestBody LoadTestRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoadTestResponse> update(
            @PathVariable String id,
            @RequestBody LoadTestRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoadTestResponse> get(@PathVariable String id) {
        return ResponseEntity.ok(service.get(id));
    }

    @GetMapping
    public ResponseEntity<List<LoadTestResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
