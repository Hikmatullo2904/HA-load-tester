package uz.hikmatullo.loadtesting.model.response;

import lombok.Builder;

@Builder
public record ExtractionRuleResponse(
        String jsonPath,
        String saveAs
) {}
