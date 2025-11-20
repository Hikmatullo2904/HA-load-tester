package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Repository;
import uz.hikmatullo.loadtesting.model.entity.LoadTest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LoadTestRepository {

    private final Map<String, LoadTest> store = new ConcurrentHashMap<>();

    public void save(LoadTest test) {
        store.put(test.getId(), test);
    }

    public Optional<LoadTest> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<LoadTest> findAll() {
        return new ArrayList<>(store.values());
    }

    public void deleteById(String id) {
        store.remove(id);
    }
}
