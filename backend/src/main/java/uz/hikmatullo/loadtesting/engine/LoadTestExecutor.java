package uz.hikmatullo.loadtesting.engine;

import uz.hikmatullo.loadtesting.model.entity.LoadTest;
import uz.hikmatullo.loadtesting.model.entity.metrics.TestExecutionReport;

public interface LoadTestExecutor {
    TestExecutionReport run(LoadTest loadTest);
}
