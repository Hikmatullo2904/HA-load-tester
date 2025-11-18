package uz.hikmatullo.loadtesting.model.response;

import lombok.Builder;

@Builder
public record ClusterInfoResponse(String id, String name, String description, String givenWorkerId) {}
