package uz.hikmatullo.loadtesting.mapper;

import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;
import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;

public class ClusterMembershipMapper {

    public static ClusterMembership toEntity(ClusterMembershipRequest req, ClusterInfoResponse info) {
        ClusterMembership membership = new ClusterMembership();
        membership.setIp(req.ip());
        membership.setPort(req.port());
        membership.setClusterId(info.id());
        membership.setClusterName(info.name());
        membership.setClusterDescription(info.description());
        membership.setGivenWorkerId(info.givenWorkerId());
        return membership;
    }

}
