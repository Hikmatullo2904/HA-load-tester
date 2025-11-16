package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.GroupMembershipRequest;

public interface ClusterMembershipService {
    void connectToMaster(GroupMembershipRequest request);
}
