package uz.hikmatullo.loadtesting.model.response;

import lombok.Builder;
import uz.hikmatullo.loadtesting.model.enums.HttpMethod;

import java.util.List;
import java.util.Map;

@Builder
public record RequestStepResponse(
        String id,
        String name,
        HttpMethod method,
        String url,
        String body,
        Map<String, String> headers,
        Map<String, String> queryParams,
        int timeoutMs,
        List<ExtractionRuleResponse> extractionRules
) {
}
