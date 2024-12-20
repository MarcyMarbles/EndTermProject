package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserRepo extends ReactiveMongoRepository<Users, String> {
    Flux<Users> findAllByDeletedAtIsNull(Pageable pageable);

    Mono<Users> findByLoginAndDeletedAtIsNull(String login);

    @Override
    @Query("{ deletedAt :  null }")
    Flux<Users> findAll();

    @Query("{ deletedAt : { $ne : null } }")
    Flux<Users> findSoftDeleted();

    Mono<Users> findByIdAndDeletedAtIsNull(String id);

    Mono<Users> findByEmailAndDeletedAtIsNull(String email);

    Mono<Users> findByUsernameAndDeletedAtIsNull(String username);
    Flux<Users> findByUsernameIsLikeIgnoreCaseAndDeletedAtIsNull(String username);
}
