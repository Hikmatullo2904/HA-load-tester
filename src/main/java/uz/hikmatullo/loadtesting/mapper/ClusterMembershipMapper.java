package uz.hikmatullo.loadtesting.mapper;

import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;
import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;

public class ClusterMembershipMapper {

    public static ClusterMembership toEntity(ClusterMembershipRequest req, ClusterInfoResponse info) {
        return new ClusterMembership(
                req.ip(),
                req.port(),
                info.id(),
                info.name(),
                info.description()
        );
    }

}
