package uz.hikmatullo.loadtesting.model.request;

public record ClusterMembershipRequest(
        String ip, int port, String clusterId
) {
}
