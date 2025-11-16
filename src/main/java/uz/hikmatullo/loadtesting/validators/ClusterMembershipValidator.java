package uz.hikmatullo.loadtesting.validators;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.exceptions.CustomBadRequestException;
import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;

@Component
public class ClusterMembershipValidator {

    public void validate(ClusterMembershipRequest request) {
        if (request.ip() == null || request.ip().isBlank())
            throw new CustomBadRequestException("IP address is required");

        if (request.port() <= 0)
            throw new CustomBadRequestException("Port must be greater than 0");

        if (request.clusterId() == null || request.clusterId().isBlank())
            throw new CustomBadRequestException("Cluster ID is required");
    }
}
