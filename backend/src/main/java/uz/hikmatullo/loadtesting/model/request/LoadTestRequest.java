package uz.hikmatullo.loadtesting.model.request;

import uz.hikmatullo.loadtesting.model.entity.LoadProfile;
import uz.hikmatullo.loadtesting.model.entity.RequestStep;
import uz.hikmatullo.loadtesting.model.entity.ValidationRule;
import uz.hikmatullo.loadtesting.model.enums.LoadTestStatus;

import java.time.Instant;
import java.util.List;

public record LoadTestRequest(
        String name,
        String description,
        LoadProfile profile,
        List<RequestStep>steps,
        List<ValidationRule> validations,
        Instant createdAt,
        LoadTestStatus status
) {
}
