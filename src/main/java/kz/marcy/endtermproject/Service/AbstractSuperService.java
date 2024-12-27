package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;

public abstract class AbstractSuperService<T> {
    public abstract Mono<T> saveEntity(T entity);
    public void softDelete(T entity) {
        if (entity instanceof AbstractSuperClass) {
            ((AbstractSuperClass) entity).setDeletedAt(Instant.now());
            saveEntity(entity).subscribe();
        } else {
            throw new IllegalArgumentException("Entity must extend AbstractSuperClass");
        }
    }

    public void softDelete(Iterable<T> entities) {
        entities.forEach(this::softDelete);
    }
}
