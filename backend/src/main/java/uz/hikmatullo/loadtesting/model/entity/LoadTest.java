package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;
import uz.hikmatullo.loadtesting.model.enums.LoadTestStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoadTest {

    private String id = UUID.randomUUID().toString();

    private String name;
    private String description;

    private LoadProfile profile;
    private List<RequestStep> steps;
    private List<ValidationRule> validations;

    private Instant createdAt;
    private LoadTestStatus status;
}
