package uz.hikmatullo.loadtesting.model.response;

import lombok.Builder;
import uz.hikmatullo.loadtesting.model.entity.LoadProfile;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.entity.ValidationRule;
import uz.hikmatullo.loadtesting.model.enums.LoadTestStatus;

import java.time.Instant;
import java.util.List;

@Builder
public record LoadTestResponse (
        String id,
        String name,
        String description,
        LoadProfileResponse profile,
        List<RequestStepResponse> steps,
        List<ValidationRuleResponse> validationRules,
        Instant createdAt,
        LoadTestStatus status,
        Instant startAt
) {
}
