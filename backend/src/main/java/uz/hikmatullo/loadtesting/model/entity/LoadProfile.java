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

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private LoadType type;

    /**
     * Used by: FIXED, RAMP_UP, SPIKE
     * Meaning: number of concurrent virtual users
     */
    private int virtualUsers;

    /**
     * Used by: FIXED, RAMP_UP, SPIKE, FIXED_RPS
     * Meaning: how long the test runs
     * Not used by BURST (because burst has no duration)
     */
    private int durationSeconds;

    /**
     * Used by: RAMP_UP
     * Meaning: time to reach the target VUs
     *---
     * Used by: SPIKE
     * Meaning: time to jump from 0 to target VUs (fast ramp)
     */
    private int rampUpSeconds;

    /**
     * Used by: FIXED_RPS
     * Meaning: steady-state requests per second controlled by scheduler
     */
    private int targetRps;

    /**
     * Used by: BURST
     * Meaning: total number of requests to fire as fast as possible
     */
    private int totalRequests;

    /**
     * Used by: BURST
     * Meaning: maximum concurrent requests allowed during the burst
     */
    private int maxConcurrency;
}

