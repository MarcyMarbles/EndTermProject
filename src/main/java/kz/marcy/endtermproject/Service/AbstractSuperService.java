package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.OffsetDateTime;

public abstract class AbstractSuperService<T> {
    public abstract Mono<T> saveEntity(T entity);

    public Mono<T> softDelete(T entity) {
        if (entity instanceof AbstractSuperClass) {
            ((AbstractSuperClass) entity).setDeletedAt(Instant.now());
            return saveEntity(entity);
        } else {
            return Mono.error(new IllegalArgumentException("Entity must extend AbstractSuperClass"));
        }
    }

    public Flux<T> softDeleteAll(Iterable<T> entities) {
        return Flux.fromIterable(entities).flatMap(this::softDelete);
    }

    public Flux<T> softDeleteAll(Flux<T> entities) {
        return entities.flatMap(this::softDelete);
    }
}
