package uz.hikmatullo.loadtesting.engine;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hikmatullo.loadtesting.engine.executors.FixedLoadTypeExecutor;
import uz.hikmatullo.loadtesting.model.entity.LoadTest;
import uz.hikmatullo.loadtesting.model.entity.metrics.TestExecutionReport;
import uz.hikmatullo.loadtesting.model.enums.LoadType;

@Service
@RequiredArgsConstructor
public class LoadTestExecutorImpl implements LoadTestExecutor {
    private final FixedLoadTypeExecutor fixedLoadTypeExecutor;
    @Override
    public TestExecutionReport run(LoadTest loadTest) {
        switch (loadTest.getProfile().getType()) {
            case LoadType.FIXED -> fixedLoadTypeExecutor.run(loadTest);
        }
        return null;
    }
}
