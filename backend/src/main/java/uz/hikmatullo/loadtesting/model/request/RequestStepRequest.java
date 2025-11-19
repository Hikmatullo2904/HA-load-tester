package uz.hikmatullo.loadtesting.model.request;

import uz.hikmatullo.loadtesting.model.enums.HttpMethod;

import java.util.List;
import java.util.Map;

public record RequestStepRequest(
        String name,
        HttpMethod method,
        String url,
        String body,
        Map<String, String> headers,
        Map<String, String> queryParams,
        int timeoutMs,
        List<ExtractionRuleRequest> extractionRules
) {
}
