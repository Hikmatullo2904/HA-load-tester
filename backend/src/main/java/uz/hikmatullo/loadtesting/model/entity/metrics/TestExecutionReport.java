package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestExecutionReport {

    private String testId;

    private GlobalMetrics global;
    private List<StepMetrics> steps;
    private List<TimelinePoint> timeline;

    private long startedAt;
    private long finishedAt;
}
