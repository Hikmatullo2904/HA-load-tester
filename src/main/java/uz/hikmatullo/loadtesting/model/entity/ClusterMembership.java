package uz.hikmatullo.loadtesting.model.entity;

public class ClusterMembership {
    private String ip;
    private int port;
    private String clusterId;
    private String clusterName;
    private String clusterDescription;

    public ClusterMembership(String ip, int port, String clusterId, String clusterName, String clusterDescription) {
        this.ip = ip;
        this.port = port;
        this.clusterId = clusterId;
        this.clusterName = clusterName;
        this.clusterDescription = clusterDescription;
    }

    public String getIp() { return ip; }
    public int getPort() { return port; }
    public String getClusterId() { return clusterId; }
    public String getClusterName() { return clusterName; }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterDescription() {
        return clusterDescription;
    }

    public void setClusterDescription(String clusterDescription) {
        this.clusterDescription = clusterDescription;
    }
}
