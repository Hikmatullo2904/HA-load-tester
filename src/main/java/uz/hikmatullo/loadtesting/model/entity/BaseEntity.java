package uz.hikmatullo.loadtesting.model.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity {
    protected final String id = UUID.randomUUID().toString();
    protected final Instant createdAt = Instant.now();

}
