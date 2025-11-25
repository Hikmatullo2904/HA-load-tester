package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.entity.metrics.TestExecutionReport;
import uz.hikmatullo.loadtesting.model.request.LoadTestRequest;
import uz.hikmatullo.loadtesting.model.response.LoadTestResponse;

import java.util.List;

public interface LoadTestService {

    LoadTestResponse create(LoadTestRequest request);

    LoadTestResponse update(String id, LoadTestRequest request);

    LoadTestResponse get(String id);

    List<LoadTestResponse> getAll();

    void delete(String id);

    TestExecutionReport execute(String id);
}
