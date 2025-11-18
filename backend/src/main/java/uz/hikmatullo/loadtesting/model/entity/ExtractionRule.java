package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtractionRule {

    // e.g. $.token or $.data.userId
    private String jsonPath;

    // variable name: token, userId, productId
    private String saveAs;
}
