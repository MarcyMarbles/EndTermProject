package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface FileDescriptorRepo extends ReactiveMongoRepository<FileDescriptor, String> {
    @Query("{ deletedAt : null }")
    Flux<FileDescriptor> findAll();

    Mono<FileDescriptor> findByPathAndDeletedAtIsNull(String path);
}
