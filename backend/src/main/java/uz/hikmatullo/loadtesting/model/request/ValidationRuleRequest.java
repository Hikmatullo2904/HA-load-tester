package uz.hikmatullo.loadtesting.model.request;

import uz.hikmatullo.loadtesting.model.enums.ValidationType;

public record ValidationRuleRequest(
        ValidationType type,
        String expectedValue
) { }
