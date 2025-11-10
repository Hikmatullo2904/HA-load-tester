package uz.hikmatullo.loadtesting.model.entity;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEntity {
    protected final String id = UUID.randomUUID().toString();
    protected final Instant createdAt = Instant.now();

    public String getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
