package uz.hikmatullo.loadtesting.service.interfaces;

import uz.hikmatullo.loadtesting.model.request.JoinMasterNodeGroupRequest;

public interface ConnectToMasterNodeService {
    void connectToMaster(JoinMasterNodeGroupRequest request);
}
