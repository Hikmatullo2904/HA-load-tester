package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;
import uz.hikmatullo.loadtesting.model.enums.HttpMethod;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestStep {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String name;
    private HttpMethod method;
    private String url;
    private String body;

    private Map<String, String> headers = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();

    private int timeoutMs;

    private List<ExtractionRule> extractionRules = new ArrayList<>();
}
