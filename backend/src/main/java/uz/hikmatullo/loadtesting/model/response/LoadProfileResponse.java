package uz.hikmatullo.loadtesting.model.response;

import lombok.Builder;
import uz.hikmatullo.loadtesting.model.enums.LoadType;

@Builder
public record LoadProfileResponse (
    String id,
    LoadType type,
    int virtualUsers,
    int durationSeconds,
    int rampUpSeconds,
    int targetRps,
    int totalRequests,
    int maxConcurrency){
}
