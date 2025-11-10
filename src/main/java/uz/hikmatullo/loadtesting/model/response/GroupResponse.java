package uz.hikmatullo.loadtesting.model.response;

import java.time.Instant;

public record GroupResponse(String id, String name, String description, Instant createdAt) {}
