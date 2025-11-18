package uz.hikmatullo.loadtesting.model.request;

import uz.hikmatullo.loadtesting.model.enums.LoadType;

public record LoadProfileRequest(
         LoadType type,
         int virtualUsers,
         int durationSeconds,
         int rampUpSeconds,
         int targetRps,
         int totalRequests,
         int maxConcurrency
) {
}
