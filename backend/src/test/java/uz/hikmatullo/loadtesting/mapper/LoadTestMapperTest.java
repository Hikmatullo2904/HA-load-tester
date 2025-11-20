package uz.hikmatullo.loadtesting.mapper;

import org.junit.jupiter.api.Test;
import uz.hikmatullo.loadtesting.model.entity.*;
import uz.hikmatullo.loadtesting.model.enums.HttpMethod;
import uz.hikmatullo.loadtesting.model.enums.LoadTestStatus;
import uz.hikmatullo.loadtesting.model.enums.LoadType;
import uz.hikmatullo.loadtesting.model.enums.ValidationType;
import uz.hikmatullo.loadtesting.model.request.*;
import uz.hikmatullo.loadtesting.model.response.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class LoadTestMapperTest {

    @Test
    void testToEntity_FullLoadTestMapping() {
        // ----- Arrange -----
        Instant now = Instant.now();

        LoadProfileRequest profileReq = new LoadProfileRequest(
                LoadType.FIXED,
                100,
                60,
                10,
                0,
                0,
                0
        );

        ExtractionRuleRequest extractionReq = new ExtractionRuleRequest("$.token", "token");
        RequestStepRequest stepReq = new RequestStepRequest(
                "Login Step",
                HttpMethod.POST,
                "https://api.test.com/login",
                "{\"user\":\"john\",\"pass\":\"123\"}",
                Map.of("Content-Type", "application/json"),
                Map.of("debug", "true"),
                5000,
                List.of(extractionReq)
        );

        ValidationRuleRequest validationReq = new ValidationRuleRequest(
                ValidationType.STATUS_CODE,
                "200"
        );

        Instant startAt = Instant.now();

        LoadTestRequest request = new LoadTestRequest(
                "Test Name",
                "Test Description",
                profileReq,
                List.of(stepReq),
                List.of(validationReq),
                startAt
        );

        // ----- Act -----
        LoadTest entity = LoadTestMapper.toEntity(request);

        // ----- Assert -----
        assertNotNull(entity);
        assertEquals("Test Name", entity.getName());
        assertEquals("Test Description", entity.getDescription());
        assertEquals(now, entity.getCreatedAt());
        assertEquals(LoadTestStatus.DRAFT, entity.getStatus());
        assertEquals(startAt, entity.getStartAt());

        // PROFILE
        LoadProfile profile = entity.getProfile();
        assertNotNull(profile);
        assertEquals(LoadType.FIXED, profile.getType());
        assertEquals(100, profile.getVirtualUsers());
        assertEquals(60, profile.getDurationSeconds());
        assertEquals(10, profile.getRampUpSeconds());

        // REQUEST STEP
        assertEquals(1, entity.getSteps().size());
        RequestStep step = entity.getSteps().getFirst();

        assertEquals("Login Step", step.getName());
        assertEquals(HttpMethod.POST, step.getMethod());
        assertEquals("https://api.test.com/login", step.getUrl());
        assertEquals("{\"user\":\"john\",\"pass\":\"123\"}", step.getBody());
        assertEquals(5000, step.getTimeoutMs());

        assertEquals("application/json", step.getHeaders().get("Content-Type"));
        assertEquals("true", step.getQueryParams().get("debug"));

        // EXTRACTION RULE
        assertEquals(1, step.getExtractionRules().size());
        ExtractionRule rule = step.getExtractionRules().getFirst();
        assertEquals("$.token", rule.getJsonPath());
        assertEquals("token", rule.getSaveAs());

        // VALIDATION RULE
        assertEquals(1, entity.getValidationRules().size());
        ValidationRule validationRule = entity.getValidationRules().getFirst();
        assertEquals(ValidationType.STATUS_CODE, validationRule.getType());
        assertEquals("200", validationRule.getExpectedValue());
    }

    @Test
    void testToResponse_LoadTestMapping() {
        // ----- Arrange -----
        LoadProfile profile = LoadProfile.builder()
                .type(LoadType.SPIKE)
                .virtualUsers(200)
                .durationSeconds(30)
                .rampUpSeconds(2)
                .targetRps(0)
                .totalRequests(0)
                .maxConcurrency(0)
                .build();

        ExtractionRule extractionRule = ExtractionRule.builder()
                .jsonPath("$.id")
                .saveAs("id")
                .build();

        RequestStep step = RequestStep.builder()
                .id("step-123")
                .name("Get Data")
                .method(HttpMethod.GET)
                .url("https://api.test.com/data")
                .headers(Map.of("Accept", "application/json"))
                .queryParams(Map.of("limit", "10"))
                .body(null)
                .timeoutMs(3000)
                .extractionRules(List.of(extractionRule))
                .build();

        ValidationRule validationRule = ValidationRule.builder()
                .type(ValidationType.LATENCY_LT)
                .expectedValue("500")
                .build();

        Instant startAt = Instant.now();
        LoadTest test = LoadTest.builder()
                .id("test-1")
                .name("Spike Test")
                .description("Testing spike load")
                .profile(profile)
                .steps(List.of(step))
                .validationRules(List.of(validationRule))
                .createdAt(Instant.now())
                .status(LoadTestStatus.RUNNING)
                .startAt(startAt)
                .build();

        // ----- Act -----
        LoadTestResponse response = LoadTestMapper.toResponse(test);

        // ----- Assert -----
        assertNotNull(response);
        assertEquals("test-1", response.id());
        assertEquals("Spike Test", response.name());
        assertEquals("Testing spike load", response.description());
        assertEquals(LoadTestStatus.RUNNING, response.status());
        assertEquals(startAt, response.startAt());

        // PROFILE RESPONSE
        LoadProfileResponse respProfile = response.profile();
        assertEquals(LoadType.SPIKE, respProfile.type());
        assertEquals(200, respProfile.virtualUsers());
        assertEquals(30, respProfile.durationSeconds());

        // STEP RESPONSE
        assertEquals(1, response.steps().size());
        RequestStepResponse stepResp = response.steps().getFirst();

        assertEquals("step-123", stepResp.id());
        assertEquals("Get Data", stepResp.name());
        assertEquals(HttpMethod.GET, stepResp.method());
        assertEquals("https://api.test.com/data", stepResp.url());
        assertEquals(3000, stepResp.timeoutMs());
        assertEquals("application/json", stepResp.headers().get("Accept"));
        assertEquals("10", stepResp.queryParams().get("limit"));

        // EXTRACTION RULE RESPONSE
        assertEquals(1, stepResp.extractionRules().size());
        ExtractionRuleResponse ruleResp = stepResp.extractionRules().getFirst();
        assertEquals("$.id", ruleResp.jsonPath());
        assertEquals("id", ruleResp.saveAs());

        // VALIDATION RULE RESPONSE
        assertEquals(1, response.validationRules().size());
        ValidationRuleResponse valResp = response.validationRules().getFirst();
        assertEquals(ValidationType.LATENCY_LT, valResp.type());
        assertEquals("500", valResp.expectedValue());
    }
}
