package uz.hikmatullo.loadtesting.model.entity.metrics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StepErrorDetail {

    private int statusCode;      // e.g., 404, 500, or 0 for exceptions
    private long count;          // how many times this distinct error occurred
    private String message;      // truncated unique error message or body snippet
}
