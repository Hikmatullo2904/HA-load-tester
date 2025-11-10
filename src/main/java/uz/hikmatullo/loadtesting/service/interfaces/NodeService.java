package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;
import java.util.List;

public interface NodeService {
    NodeResponse connect(NodeConnectRequest request);
    List<NodeResponse> getNodesByGroup(String groupId);
}
