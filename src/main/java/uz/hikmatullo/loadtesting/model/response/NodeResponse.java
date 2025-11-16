package uz.hikmatullo.loadtesting.model.response;

import java.time.Instant;

public record NodeResponse(
        String id,
        String clusterId,
        String host,
        Instant connectedAt
) {}
