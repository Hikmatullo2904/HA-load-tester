package uz.hikmatullo.loadtesting.repository;

import org.springframework.stereotype.Repository;
import uz.hikmatullo.loadtesting.model.entity.Group;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GroupRepository {

    private final Map<String, Group> store = new ConcurrentHashMap<>();

    public List<Group> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Group> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Group save(Group group) {
        store.put(group.getId(), group);
        return group;
    }

    public void deleteById(String id) {
        store.remove(id);
    }
}