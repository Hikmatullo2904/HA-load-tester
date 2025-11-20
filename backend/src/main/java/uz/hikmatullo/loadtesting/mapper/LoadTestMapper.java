package uz.hikmatullo.loadtesting.mapper;


import uz.hikmatullo.loadtesting.model.entity.*;
import uz.hikmatullo.loadtesting.model.enums.LoadTestStatus;
import uz.hikmatullo.loadtesting.model.request.*;
import uz.hikmatullo.loadtesting.model.response.*;

import java.time.Instant;
import java.util.List;

public class LoadTestMapper {

    public static LoadTest toEntity(LoadTestRequest request) {
        return LoadTest.builder()
                .name(request.name())
                .description(request.description())
                .profile(toEntity(request.profile()))
                .steps(toRequestSteps(request.steps()))
                .validationRules(toValidationRules(request.validationRules()))
                .startAt(request.startAt())
                .createdAt(Instant.now())
                .status(LoadTestStatus.DRAFT)
                .build();
    }

    public static LoadProfile toEntity(LoadProfileRequest request) {
        return LoadProfile.builder()
                .type(request.type())
                .virtualUsers(request.virtualUsers())
                .durationSeconds(request.durationSeconds())
                .rampUpSeconds(request.rampUpSeconds())
                .targetRps(request.targetRps())
                .totalRequests(request.totalRequests())
                .maxConcurrency(request.maxConcurrency())
                .build();
    }

    public static List<RequestStep> toRequestSteps(List<RequestStepRequest> list) {
        return list.stream()
                .map(LoadTestMapper::toRequestStep)
                .toList();
    }

    public static RequestStep toRequestStep(RequestStepRequest request) {
        return RequestStep.builder()
                .name(request.name())
                .method(request.method())
                .url(request.url())
                .body(request.body())
                .headers(request.headers())
                .queryParams(request.queryParams())
                .timeoutMs(request.timeoutMs())
                .extractionRules(toExtractionRules(request.extractionRules()))
                .build();
    }

    public static List<ExtractionRule> toExtractionRules(List<ExtractionRuleRequest> list) {
        return list.stream()
                .map(LoadTestMapper::toExtractionRule)
                .toList();
    }

    public static ExtractionRule toExtractionRule(ExtractionRuleRequest request) {
        return ExtractionRule.builder()
                .jsonPath(request.jsonPath())
                .saveAs(request.saveAs())
                .build();
    }

    public static List<ValidationRule> toValidationRules(List<ValidationRuleRequest> list) {
        return list.stream()
                .map(LoadTestMapper::toValidationRule)
                .toList();
    }

    public static ValidationRule toValidationRule(ValidationRuleRequest request) {
        return ValidationRule.builder()
                .type(request.type())
                .expectedValue(request.expectedValue())
                .build();
    }

    public static LoadTestResponse toResponse(LoadTest entity) {
        return LoadTestResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .profile(toProfileResponse(entity.getProfile()))
                .steps(toRequestStepResponses(entity.getSteps()))
                .validationRules(toValidationRuleResponses(entity.getValidationRules()))
                .createdAt(entity.getCreatedAt())
                .status(entity.getStatus())
                .startAt(entity.getStartAt())
                .build();
    }

    public static LoadProfileResponse toProfileResponse(LoadProfile profile) {
        return LoadProfileResponse.builder()
                .id(profile.getId())
                .type(profile.getType())
                .virtualUsers(profile.getVirtualUsers())
                .durationSeconds(profile.getDurationSeconds())
                .rampUpSeconds(profile.getRampUpSeconds())
                .targetRps(profile.getTargetRps())
                .totalRequests(profile.getTotalRequests())
                .maxConcurrency(profile.getMaxConcurrency())
                .build();
    }

    public static List<RequestStepResponse> toRequestStepResponses(List<RequestStep> list) {
        return list.stream()
                .map(LoadTestMapper::toRequestStepResponse)
                .toList();
    }

    public static RequestStepResponse toRequestStepResponse(RequestStep step) {
        return RequestStepResponse.builder()
                .id(step.getId())
                .name(step.getName())
                .method(step.getMethod())
                .url(step.getUrl())
                .body(step.getBody())
                .headers(step.getHeaders())
                .queryParams(step.getQueryParams())
                .timeoutMs(step.getTimeoutMs())
                .extractionRules(toExtractionRuleResponses(step.getExtractionRules()))
                .build();
    }

    public static List<ExtractionRuleResponse> toExtractionRuleResponses(List<ExtractionRule> list) {
        return list.stream()
                .map(LoadTestMapper::toExtractionRuleResponse)
                .toList();
    }

    public static ExtractionRuleResponse toExtractionRuleResponse(ExtractionRule rule) {
        return ExtractionRuleResponse.builder()
                .jsonPath(rule.getJsonPath())
                .saveAs(rule.getSaveAs())
                .build();
    }

    public static List<ValidationRuleResponse> toValidationRuleResponses(List<ValidationRule> list) {
        return list.stream()
                .map(LoadTestMapper::toValidationRuleResponse)
                .toList();
    }

    public static ValidationRuleResponse toValidationRuleResponse(ValidationRule rule) {
        return ValidationRuleResponse.builder()
                .type(rule.getType())
                .expectedValue(rule.getExpectedValue())
                .build();
    }


}
