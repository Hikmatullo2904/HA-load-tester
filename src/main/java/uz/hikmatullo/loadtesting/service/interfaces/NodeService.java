package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;
import uz.hikmatullo.loadtesting.model.response.NodeResponse;
import java.util.List;

public interface NodeService {
    ClusterInfoResponse addWorkerNode(NodeConnectRequest request);
    List<NodeResponse> getNodesByGroup(String groupId);
}
