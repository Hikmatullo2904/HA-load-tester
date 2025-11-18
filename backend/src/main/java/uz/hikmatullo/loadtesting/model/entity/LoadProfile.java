package uz.hikmatullo.loadtesting.model.entity;

import lombok.*;
import uz.hikmatullo.loadtesting.model.enums.LoadType;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoadProfile {

    private String id = UUID.randomUUID().toString();

    private LoadType type;

    private int virtualUsers;

    private int durationSeconds;

    private int rampUpSeconds;

    // Only used for FIXED_RPS type
    private int targetRps;

    // For BURST only
    private int totalRequests;
    // for BURST
    private int maxConcurrency;
}
