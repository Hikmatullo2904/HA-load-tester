package uz.hikmatullo.loadtesting.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.mapper.LoadTestMapper;
import uz.hikmatullo.loadtesting.model.entity.LoadTest;
import uz.hikmatullo.loadtesting.model.request.LoadTestRequest;
import uz.hikmatullo.loadtesting.model.response.LoadTestResponse;
import uz.hikmatullo.loadtesting.repository.LoadTestRepository;
import uz.hikmatullo.loadtesting.service.interfaces.LoadTestService;

import java.util.List;

@Service
@Slf4j
public class LoadTestServiceImpl implements LoadTestService {

    private final LoadTestRepository repository;

    public LoadTestServiceImpl(LoadTestRepository repository) {
        this.repository = repository;
    }

    @Override
    public LoadTestResponse create(LoadTestRequest request) {
        log.info("Creating LoadTest: name={}, description={}", request.name(), request.description());

        LoadTest entity = LoadTestMapper.toEntity(request);

        repository.save(entity);

        log.info("LoadTest created successfully. id={}", entity.getId());
        return LoadTestMapper.toResponse(entity);
    }

    @Override
    public LoadTestResponse update(String id, LoadTestRequest request) {
        log.info("Updating LoadTest: id={}", id);

        repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LoadTest not found: " + id));

        LoadTest updated = LoadTestMapper.toEntity(request);
        updated.setId(id);

        repository.save(updated);

        log.info("LoadTest updated successfully. id={}", id);
        return LoadTestMapper.toResponse(updated);
    }

    @Override
    public LoadTestResponse get(String id) {
        LoadTest loadTest = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LoadTest not found: " + id));
        return LoadTestMapper.toResponse(loadTest);
    }

    @Override
    public List<LoadTestResponse> getAll() {
        return repository.findAll().stream()
                .map(LoadTestMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(String id) {
        log.info("Deleting LoadTest: id={}", id);

        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("LoadTest not found: " + id);
        }

        repository.deleteById(id);
        log.info("LoadTest deleted. id={}", id);
    }
}
