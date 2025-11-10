package uz.hikmatullo.loadtesting.model.response;

import java.time.Instant;

public record NodeResponse(
        String id,
        String groupId,
        String host,
        Instant connectedAt
) {}
