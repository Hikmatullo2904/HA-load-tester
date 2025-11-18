package uz.hikmatullo.loadtesting.mapper;

import org.junit.jupiter.api.Test;
import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;
import uz.hikmatullo.loadtesting.model.request.ClusterMembershipRequest;
import uz.hikmatullo.loadtesting.model.response.ClusterInfoResponse;


import static org.junit.jupiter.api.Assertions.*;

class ClusterMembershipMapperTest {

    @Test
    void toEntity_shouldMapAllFieldsCorrectly() {
        // given
        ClusterMembershipRequest req = new ClusterMembershipRequest(
                "192.168.0.10",
                8080,
                "cluster-123"
        );

        ClusterInfoResponse info = new ClusterInfoResponse(
                "cluster-123",
                "Test Cluster",
                "A simple cluster",
                "worker-999"
        );

        // when
        ClusterMembership result = ClusterMembershipMapper.toEntity(req, info);

        // then
        assertNotNull(result);

        assertEquals("192.168.0.10", result.getIp());
        assertEquals(8080, result.getPort());

        assertEquals("cluster-123", result.getClusterId());
        assertEquals("Test Cluster", result.getClusterName());
        assertEquals("A simple cluster", result.getClusterDescription());
        assertEquals("worker-999", result.getGivenWorkerId());
    }


    @Test
    void toEntity_shouldHandleEmptyStrings() {
        // given
        ClusterMembershipRequest req = new ClusterMembershipRequest(
                "", 0, ""
        );

        ClusterInfoResponse info = new ClusterInfoResponse(
                "", "", "", ""
        );

        // when
        ClusterMembership result = ClusterMembershipMapper.toEntity(req, info);

        // then
        assertNotNull(result);

        assertEquals("", result.getIp());
        assertEquals(0, result.getPort());
        assertEquals("", result.getClusterId());
        assertEquals("", result.getClusterName());
        assertEquals("", result.getClusterDescription());
        assertEquals("", result.getGivenWorkerId());
    }


    @Test
    void toEntity_shouldNotReturnNull() {
        ClusterMembershipRequest req = new ClusterMembershipRequest("1.1.1.1", 9000, "id1");
        ClusterInfoResponse info = new ClusterInfoResponse("id1", "name", "desc", "worker1");

        ClusterMembership result = ClusterMembershipMapper.toEntity(req, info);

        assertNotNull(result);
    }
}
