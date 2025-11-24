package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkerMetricsBatch {

    private String workerId;
    private String testId;
    // epoch seconds (for timeline)
    private long timestampSecond;
    private List<RequestMetrics> metrics;
}
