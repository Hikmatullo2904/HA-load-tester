package uz.hikmatullo.loadtesting.model.request;

import java.util.List;
import java.util.Map;

public record RequestStepRequest(
        String name,
        String method,
        String url,
        String body,
        Map<String, String> headers,
        Map<String, String> queryParams,
        int timeoutMs,
        List<ExtractionRuleRequest> extract,
        List<ValidationRuleRequest> validate
) {
}
