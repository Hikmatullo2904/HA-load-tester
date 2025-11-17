package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Component;
import uz.hikmatullo.loadtesting.model.entity.Cluster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClusterRepository {

    private final Map<String, Cluster> store = new ConcurrentHashMap<>();

    public List<Cluster> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Cluster> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Cluster save(Cluster cluster) {
        store.put(cluster.getId(), cluster);
        return cluster;
    }

    public void deleteById(String id) {
        store.remove(id);
    }
}