package uz.hikmatullo.loadtesting.service.interfaces;

public interface MasterNodeService {
    void connectToMaster(String ip, int port, String groupId, String nodeName, String host);
}
