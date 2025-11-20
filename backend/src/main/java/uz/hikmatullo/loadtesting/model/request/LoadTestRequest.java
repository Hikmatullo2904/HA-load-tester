package uz.hikmatullo.loadtesting.model.request;

import uz.hikmatullo.loadtesting.model.enums.LoadTestStatus;

import java.time.Instant;
import java.util.List;

public record LoadTestRequest(
        String name,
        String description,
        LoadProfileRequest profile,
        List<RequestStepRequest> steps,
        List<ValidationRuleRequest> validationRules,
        Instant startAt
) {
}
