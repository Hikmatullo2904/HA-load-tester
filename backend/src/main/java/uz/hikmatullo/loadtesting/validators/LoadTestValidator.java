package uz.hikmatullo.loadtesting.validators;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.exceptions.CustomBadRequestException;
import uz.hikmatullo.loadtesting.model.enums.LoadType;
import uz.hikmatullo.loadtesting.model.enums.ValidationType;
import uz.hikmatullo.loadtesting.model.request.*;

import java.util.List;

@Component
public class LoadTestValidator {

    public void validateForUpdate(LoadTestRequest request) {
        validateForCreate(request);
    }

    public void validateForCreate(LoadTestRequest request) {
        if (request == null) {
            throw new CustomBadRequestException("Request cannot be null");
        }

        validateBasicFields(request);
        validateProfile(request.profile());
        validateSteps(request.steps());
        validateValidationRules(request.validationRules());
    }

    private void validateBasicFields(LoadTestRequest request) {
        if (isBlank(request.name())) {
            throw new CustomBadRequestException("Test name is required");
        }
        if (request.profile() == null) {
            throw new CustomBadRequestException("Load profile is required");
        }
        if (request.steps() == null || request.steps().isEmpty()) {
            throw new CustomBadRequestException("At least one request step is required");
        }
    }

    private void validateProfile(LoadProfileRequest profile) {
        if (profile == null) {
            throw new CustomBadRequestException("LoadProfile is required");
        }
        LoadType type = profile.type();
        if (type == null) {
            throw new CustomBadRequestException("LoadType is required in LoadProfile");
        }

        switch (type) {
            case FIXED -> validateFixedProfile(profile);
            case RAMP_UP -> validateRampUpProfile(profile);
            case SPIKE -> validateSpikeProfile(profile);
            case FIXED_RPS -> validateFixedRpsProfile(profile);
            case BURST -> validateBurstProfile(profile);
            default -> throw new CustomBadRequestException("Unsupported LoadType: " + type);
        }
    }

    private void validateFixedProfile(LoadProfileRequest profile) {
        if (profile.virtualUsers() <= 0) {
            throw new CustomBadRequestException("virtualUsers must be > 0 for FIXED tests");
        }
        if (profile.durationSeconds() <= 0) {
            throw new CustomBadRequestException("durationSeconds must be > 0 for FIXED tests");
        }
        // rampUp, targetRps, burst fields are ignored for FIXED
    }

    private void validateRampUpProfile(LoadProfileRequest profile) {
        if (profile.virtualUsers() <= 0) {
            throw new CustomBadRequestException("virtualUsers must be > 0 for RAMP_UP tests");
        }
        if (profile.durationSeconds() <= 0) {
            throw new CustomBadRequestException("durationSeconds must be > 0 for RAMP_UP tests");
        }
        if (profile.rampUpSeconds() < 0) {
            throw new CustomBadRequestException("rampUpSeconds must be >= 0 for RAMP_UP tests");
        }
    }

    private void validateSpikeProfile(LoadProfileRequest profile) {
        if (profile.virtualUsers() <= 0) {
            throw new CustomBadRequestException("virtualUsers must be > 0 for SPIKE tests");
        }
        if (profile.durationSeconds() <= 0) {
            throw new CustomBadRequestException("durationSeconds must be > 0 for SPIKE tests");
        }
        if (profile.rampUpSeconds() < 0) {
            throw new CustomBadRequestException("rampUpSeconds must be >= 0 for SPIKE tests");
        }
        // for spike, rampUpSeconds is expected to be small; we do not enforce an upper limit here
    }

    private void validateFixedRpsProfile(LoadProfileRequest profile) {
        if (profile.targetRps() <= 0) {
            throw new CustomBadRequestException("targetRps must be > 0 for FIXED_RPS tests");
        }
        if (profile.durationSeconds() <= 0) {
            throw new CustomBadRequestException("durationSeconds must be > 0 for FIXED_RPS tests");
        }
        // virtualUsers, rampUp are not required for FIXED_RPS (executor manages concurrency)
    }

    private void validateBurstProfile(LoadProfileRequest profile) {
        if (profile.totalRequests() <= 0) {
            throw new CustomBadRequestException("totalRequests must be > 0 for BURST tests");
        }
        if (profile.maxConcurrency() <= 0) {
            throw new CustomBadRequestException("maxConcurrency must be > 0 for BURST tests");
        }
        if (profile.maxConcurrency() > profile.totalRequests()) {
            throw new CustomBadRequestException("maxConcurrency cannot be greater than totalRequests for BURST tests");
        }
        // durationSeconds and targetRps are ignored for BURST
    }

    private void validateSteps(List<RequestStepRequest> steps) {
        if (steps == null || steps.isEmpty()) {
            throw new CustomBadRequestException("At least one RequestStep is required");
        }

        for (int i = 0; i < steps.size(); i++) {
            RequestStepRequest step = steps.get(i);
            String ctx = "Step[" + i + "] ";

            if (step == null) {
                throw new CustomBadRequestException(ctx + "request step is null");
            }
            if (isBlank(step.name())) {
                throw new CustomBadRequestException(ctx + "name is required");
            }
            if (step.method() == null) {
                throw new CustomBadRequestException(ctx + "HTTP method is required");
            }
            if (isBlank(step.url())) {
                throw new CustomBadRequestException(ctx + "url is required");
            }
            if (step.timeoutMs() < 0) {
                throw new CustomBadRequestException(ctx + "timeoutMs must be >= 0");
            }

            // Headers and query params can be null or empty; validate their keys if present
            if (step.headers() != null) {
                step.headers().forEach((k, v) -> {
                    if (isBlank(k)) {
                        throw new CustomBadRequestException(ctx + "headers must not have empty names");
                    }
                });
            }
            if (step.queryParams() != null) {
                step.queryParams().forEach((k, v) -> {
                    if (isBlank(k)) {
                        throw new CustomBadRequestException(ctx + "queryParams must not have empty names");
                    }
                });
            }

            // Extraction rules
            validateExtractionRules(step.extractionRules(), ctx);

            // Body/template validation: if method is GET/DELETE, body should normally be empty (not enforced strictly)
            if ((step.method().name().equalsIgnoreCase("GET") || step.method().name().equalsIgnoreCase("DELETE"))
                    && step.body() != null && !step.body().isBlank()) {
                // we allow body but warn - here we throw to be strict; you may change this to log a warning instead
                throw new CustomBadRequestException(ctx + "GET/DELETE request contains a body; this is unusual");
            }
        }
    }

    private void validateExtractionRules(List<ExtractionRuleRequest> list, String ctx) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ExtractionRuleRequest r = list.get(i);
            String rctx = ctx + "ExtractionRule[" + i + "] ";
            if (r == null) {
                throw new CustomBadRequestException(rctx + "is null");
            }
            if (isBlank(r.jsonPath())) {
                throw new CustomBadRequestException(rctx + "jsonPath is required");
            }
            if (isBlank(r.saveAs())) {
                throw new CustomBadRequestException(rctx + "saveAs is required");
            }
            // simple saveAs name validation: letters, digits, underscore only
            if (!r.saveAs().matches("[A-Za-z0-9_]+")) {
                throw new CustomBadRequestException(rctx + "saveAs must contain only letters, digits or underscore");
            }
        }
    }

    private void validateValidationRules(List<ValidationRuleRequest> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        for (int i = 0; i < list.size(); i++) {
            ValidationRuleRequest r = list.get(i);
            String ctx = "ValidationRule[" + i + "] ";
            if (r == null) {
                throw new CustomBadRequestException(ctx + "is null");
            }
            if (r.type() == null) {
                throw new CustomBadRequestException(ctx + "type is required");
            }
            if (isBlank(r.expectedValue())) {
                throw new CustomBadRequestException(ctx + "expectedValue is required");
            }
            // Optionally enforce certain types expect numeric values (e.g., LATENCY_LT)
            if (r.type() == ValidationType.LATENCY_LT) {
                try {
                    int v = Integer.parseInt(r.expectedValue());
                    if (v < 0) {
                        throw new CustomBadRequestException(ctx + "expectedValue must be a non-negative integer for LATENCY_LT");
                    }
                } catch (NumberFormatException ex) {
                    throw new CustomBadRequestException(ctx + "expectedValue must be an integer for LATENCY_LT");
                }
            }
        }
    }

    // small helper
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
