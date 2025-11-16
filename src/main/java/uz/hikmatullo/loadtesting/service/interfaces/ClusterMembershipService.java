package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;

public interface ClusterMembershipService {
    void connectToCluster(ClusterMembershipRequest request);
}
