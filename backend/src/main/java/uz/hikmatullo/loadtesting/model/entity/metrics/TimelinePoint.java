package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimelinePoint {

    private long timestampSecond;

    // total requests in this second
    private long requests;
    private long successes;
    private long failures;

    private double rps;
}
