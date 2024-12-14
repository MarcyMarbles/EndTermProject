package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Roles;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface RolesRepo extends ReactiveMongoRepository<Roles, String> {
    Mono<Boolean> existsByCodeAndDeletedAtIsNull(String code);
    Flux<Roles> findRolesByCodeInAndDeletedAtIsNull(List<String> codes);
    @Query("{ deletedAt :  null }")
    Flux<Roles> findAll();
    Mono<Roles> findByCodeAndDeletedAtIsNull(String code);
}
