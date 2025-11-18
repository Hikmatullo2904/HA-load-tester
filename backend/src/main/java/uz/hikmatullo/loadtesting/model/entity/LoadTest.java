package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;
import uz.hikmatullo.loadtesting.model.enums.LoadTestStatus;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoadTest {

    private String id;

    private String name;
    private String description;

    private LoadProfile profile;
    private List<RequestStep> steps;
    private List<ValidationRule> validations;

    private Instant createdAt;
    private LoadTestStatus status;
}
