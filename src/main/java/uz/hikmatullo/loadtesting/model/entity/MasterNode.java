package uz.hikmatullo.loadtesting.model.entity;

public class MasterNode {
    private String masterIp;
    private int masterPort;
    private String groupId;
    private String groupName;

    public MasterNode(String masterIp, int masterPort, String groupId, String groupName) {
        this.masterIp = masterIp;
        this.masterPort = masterPort;
        this.groupId = groupId;
        this.groupName = groupName;
    }

    public String getMasterIp() { return masterIp; }
    public int getMasterPort() { return masterPort; }
    public String getGroupId() { return groupId; }
    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
