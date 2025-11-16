package uz.hikmatullo.loadtesting.model.request;

public record GroupMembershipRequest(
        String ip, int port, String groupId
) {
}
