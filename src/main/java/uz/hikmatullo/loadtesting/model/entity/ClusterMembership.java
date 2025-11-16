package uz.hikmatullo.loadtesting.model.entity;

public class ClusterMembership {
    private String ip;
    private int port;
    private String groupId;
    private String groupName;
    private String groupDescription;

    public ClusterMembership(String ip, int port, String groupId, String groupName, String groupDescription) {
        this.ip = ip;
        this.port = port;
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDescription = groupDescription;
    }

    public String getIp() { return ip; }
    public int getPort() { return port; }
    public String getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }
}
