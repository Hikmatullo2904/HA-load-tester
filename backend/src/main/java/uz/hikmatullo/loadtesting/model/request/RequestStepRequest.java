package uz.hikmatullo.loadtesting.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.hikmatullo.loadtesting.model.enums.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestStepRequest {
    private String name;
    private HttpMethod method;
    private String url;
    private String body;
    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private int timeoutMs;
    private List<ExtractionRuleRequest> extractionRules = new ArrayList<>();
}
