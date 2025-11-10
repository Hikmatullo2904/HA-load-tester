package uz.hikmatullo.loadtesting.model.request;

public record NodeConnectRequest(
        String groupId,
        String name,
        String host
) {}
