package uz.hikmatullo.loadtesting.model.response;

import java.time.Instant;

public record ClusterResponse(String id, String name, String description, Instant createdAt) {}
