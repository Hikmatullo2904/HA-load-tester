package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;
import uz.hikmatullo.loadtesting.model.enums.ValidationType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationRule {

    private ValidationType type;
    private String expectedValue;
}
