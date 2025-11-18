package uz.hikmatullo.loadtesting.mapper;

import org.junit.jupiter.api.Test;
import uz.hikmatullo.loadtesting.model.entity.WorkerNode;
import uz.hikmatullo.loadtesting.model.enums.WorkerStatusEnum;
import uz.hikmatullo.loadtesting.model.response.WorkerNodeResponse;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class NodeMapperTest {

    @Test
    void toResponse_shouldMapFieldsCorrectly() {
        // given
        WorkerNode node = new WorkerNode("cluster-123", "192.168.1.10");
        node.setStatus(WorkerStatusEnum.DISCONNECTED);
        Instant connectedAt = node.getConnectedAt();

        // when
        WorkerNodeResponse response = NodeMapper.toResponse(node);

        // then
        assertNotNull(response);

        assertEquals(node.getId(), response.id());
        assertEquals("cluster-123", response.clusterId());
        assertEquals("192.168.1.10", response.ip());
        assertEquals(connectedAt, response.connectedAt());
        assertEquals(WorkerStatusEnum.DISCONNECTED, response.status());
    }

    @Test
    void toResponse_shouldWorkWithNullStatus() {
        // given
        WorkerNode node = new WorkerNode("cluster-456", "10.10.10.10");
        node.setStatus(null); // simulate null state

        // when
        WorkerNodeResponse response = NodeMapper.toResponse(node);

        // then
        assertNotNull(response);
        assertNull(response.status());
    }

    @Test
    void toResponse_shouldNotReturnNull() {
        WorkerNode node = new WorkerNode("c1", "1.1.1.1");

        WorkerNodeResponse response = NodeMapper.toResponse(node);

        assertNotNull(response);
    }
}
