package uz.hikmatullo.loadtesting.model.request;

public record JoinMasterNodeGroupRequest(
        String ip, int port, String groupId
) {
}
