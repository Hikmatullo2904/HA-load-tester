package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;
import uz.hikmatullo.loadtesting.model.enums.HttpMethod;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestStep {

    private String id;

    private String name;
    private HttpMethod method;
    private String url;
    private String body;

    private Map<String, String> headers;
    private Map<String, String> queryParams;

    private int timeoutMs;
}
