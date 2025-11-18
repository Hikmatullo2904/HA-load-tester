package uz.hikmatullo.loadtesting.model.response;

import lombok.Builder;
import uz.hikmatullo.loadtesting.model.enums.ValidationType;

@Builder
public record ValidationRuleResponse(
        ValidationType type,
        String expectedValue
) {
}
