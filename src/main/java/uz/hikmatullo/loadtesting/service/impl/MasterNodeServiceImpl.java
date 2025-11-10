package uz.hikmatullo.loadtesting.service.impl;

import uz.hikmatullo.loadtesting.model.entity.MasterNode;
import uz.hikmatullo.loadtesting.model.request.NodeConnectRequest;
import uz.hikmatullo.loadtesting.model.response.GroupInfoResponse;
import uz.hikmatullo.loadtesting.service.interfaces.MasterNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MasterNodeServiceImpl implements MasterNodeService {

    private static final Logger log = LoggerFactory.getLogger(MasterNodeServiceImpl.class);
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MasterNode masterNode;

    @Override
    public void connectToMaster(String ip, int port, String groupId, String nodeName, String host) {
        try {
            String url = "http://" + ip + ":" + port + "/api/nodes/connect";
            log.info("Connecting to master={} groupId={} ...", url, groupId);

            NodeConnectRequest req = new NodeConnectRequest(groupId, nodeName, host);
            String json = objectMapper.writeValueAsString(req);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 201) {
                GroupInfoResponse groupInfo = objectMapper.readValue(response.body(), GroupInfoResponse.class);
                masterNode = new MasterNode(ip, port, groupInfo.groupId(), groupInfo.groupName());
                log.info("Successfully connected to master group='{}' ({})", groupInfo.groupName(), groupInfo.groupId());
            } else {
                log.error("Failed to connect to master. HTTP status: {} body: {}", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            log.error("Connection to master failed: {}", e.getMessage(), e);
        }
    }

    public MasterNode getConnectedMaster() {
        return masterNode;
    }
}
