package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.ClusterCreateRequest;
import uz.hikmatullo.loadtesting.model.request.ClusterUpdateRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterResponse;

import java.util.List;

public interface ClusterService {
    ClusterResponse create(ClusterCreateRequest req);
    List<ClusterResponse> getAll();
    ClusterResponse getById(String id);
    ClusterResponse update(String id, ClusterUpdateRequest req);
    void delete(String id);

    List<ClusterResponse> getConnectedGroups();
}
