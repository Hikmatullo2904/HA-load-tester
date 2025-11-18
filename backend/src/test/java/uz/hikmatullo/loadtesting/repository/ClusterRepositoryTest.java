package uz.hikmatullo.loadtesting.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uz.hikmatullo.loadtesting.model.entity.Cluster;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClusterRepositoryTest {

    private ClusterRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ClusterRepository();
    }

    @Test
    void save_shouldStoreCluster() {
        // given
        Cluster cluster = new Cluster("My Cluster", "Test description");

        // when
        repository.save(cluster);

        // then
        Optional<Cluster> found = repository.findById(cluster.getId());

        assertTrue(found.isPresent());
        assertEquals("My Cluster", found.get().getName());
        assertEquals("Test description", found.get().getDescription());
    }

    @Test
    void findAll_shouldReturnAllClusters() {
        // given
        Cluster c1 = new Cluster("Cluster A", "Desc A");

        Cluster c2 = new Cluster("Cluster B", "Desc B");


        repository.save(c1);
        repository.save(c2);

        List<Cluster> all = repository.findAll();

        assertEquals(2, all.size());
    }

    @Test
    void findById_shouldReturnEmptyIfNotExists() {
        Optional<Cluster> result = repository.findById("unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteById_shouldRemoveCluster() {
        // given
        Cluster cluster = new Cluster("To Delete", "desc");
        repository.save(cluster);

        // when
        repository.deleteById(cluster.getId());

        // then
        assertTrue(repository.findById(cluster.getId()).isEmpty());
        assertEquals(0, repository.findAll().size());
    }
}
