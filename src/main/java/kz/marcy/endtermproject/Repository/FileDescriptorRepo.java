package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.FileDescriptor;
import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@Repository
public interface FileDescriptorRepo extends ReactiveMongoRepository<FileDescriptor, String> {
    @Query("{ deletedAt : null }")
    Flux<FileDescriptor> findAll();

    Mono<FileDescriptor> findByPathAndDeletedAtIsNull(String path);

    Flux<FileDescriptor> findAllByIdAndDeletedAtIsNull(String s);
}
