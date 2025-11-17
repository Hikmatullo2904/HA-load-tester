package uz.hikmatullo.loadtesting.model.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClusterMembership {
    private String ip;
    private int port;
    private String clusterId;
    private String clusterName;
    private String clusterDescription;
    private String givenWorkerId;
}
