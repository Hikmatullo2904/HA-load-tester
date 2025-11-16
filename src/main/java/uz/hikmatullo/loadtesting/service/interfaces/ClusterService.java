package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.GroupCreateRequest;
import uz.hikmatullo.loadtesting.model.request.GroupUpdateRequest;
import uz.hikmatullo.loadtesting.model.response.GroupResponse;

import java.util.List;

public interface ClusterService {
    GroupResponse create(GroupCreateRequest req);
    List<GroupResponse> getAll();
    GroupResponse getById(String id);
    GroupResponse update(String id, GroupUpdateRequest req);
    void delete(String id);

    List<GroupResponse> getConnectedGroups();
}
