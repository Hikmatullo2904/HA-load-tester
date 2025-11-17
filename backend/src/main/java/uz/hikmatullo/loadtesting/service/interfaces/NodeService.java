package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;
import uz.hikmatullo.loadtesting.model.response.WorkerNodeResponse;
import java.util.List;

public interface NodeService {
    ClusterInfoResponse addWorkerNode(NodeConnectRequest request);
    List<WorkerNodeResponse> getNodesByCluster(String groupId);
}
