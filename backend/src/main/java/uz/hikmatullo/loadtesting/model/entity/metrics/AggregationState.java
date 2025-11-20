package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AggregationState {

    private String testId;

    private Map<String, List<RequestMetric>> perStepRawMetrics = new HashMap<>();

    private Map<Long, TimelinePoint> timeline = new HashMap<>();

    private long startedAt;
    private long finishedAt;
}
