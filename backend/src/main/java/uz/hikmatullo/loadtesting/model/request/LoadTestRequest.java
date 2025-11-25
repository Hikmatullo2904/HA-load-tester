package uz.hikmatullo.loadtesting.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoadTestRequest{
    private String name;
    private String description;
    private LoadProfileRequest profile;
    private List<RequestStepRequest> steps = new ArrayList<>();
    private List<ValidationRuleRequest> validationRules = new ArrayList<>();
    private Instant startAt;
}
