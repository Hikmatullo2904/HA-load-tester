package uz.hikmatullo.loadtesting.validators;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.exceptions.CustomBadRequestException;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;

@Component
public class NodeValidator {

    public void validateConnectRequest(NodeConnectRequest request) {
        if (request.clusterId() == null || request.clusterId().isBlank()) {
            throw new CustomBadRequestException("Group ID is null");
        }
    }

}
