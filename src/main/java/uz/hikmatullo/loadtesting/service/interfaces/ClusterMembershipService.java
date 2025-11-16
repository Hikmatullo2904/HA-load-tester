package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;

public interface ClusterMembershipService {
    void connectToMaster(ClusterMembershipRequest request);
}
