package uz.hikmatullo.loadtesting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.hikmatullo.loadtesting.model.entity.ClusterMembership;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClusterMembershipRepositoryTest {

    private ClusterMembershipRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ClusterMembershipRepository();
    }

    @Test
    void saveMasterNode_shouldStoreMembership() {
        // given
        ClusterMembership membership = new ClusterMembership();
        membership.setClusterId("cluster-1");
        membership.setClusterName("Test Cluster");
        membership.setClusterDescription("Description");
        membership.setIp("127.0.0.1");
        membership.setPort(8080);
        membership.setGivenWorkerId("worker-10");

        // when
        repository.saveMasterNode(membership);

        // then
        Optional<ClusterMembership> found = repository.findMasterByGroupId("cluster-1");

        assertTrue(found.isPresent());
        assertEquals("Test Cluster", found.get().getClusterName());
        assertEquals("127.0.0.1", found.get().getIp());
        assertEquals(8080, found.get().getPort());
        assertEquals("worker-10", found.get().getGivenWorkerId());
    }

    @Test
    void findMasterByGroupId_shouldReturnEmptyForMissingKey() {
        // when
        Optional<ClusterMembership> result = repository.findMasterByGroupId("unknown");

        // then
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllMasterNodes_shouldReturnAllStoredMemberships() {
        // given
        ClusterMembership m1 = new ClusterMembership();
        m1.setClusterId("c1");
        repository.saveMasterNode(m1);

        ClusterMembership m2 = new ClusterMembership();
        m2.setClusterId("c2");
        repository.saveMasterNode(m2);

        // when
        List<ClusterMembership> all = repository.findAllMasterNodes();

        // then
        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(m -> "c1".equals(m.getClusterId())));
        assertTrue(all.stream().anyMatch(m -> "c2".equals(m.getClusterId())));
    }
}
