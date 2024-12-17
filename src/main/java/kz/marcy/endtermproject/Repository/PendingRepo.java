package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Service.PendingCodes;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface PendingRepo extends ReactiveMongoRepository<PendingCodes, String> {
    Mono<PendingCodes> findByCodeAndUsedIsFalseAndDueDateAfter(String code, Instant now);

    Mono<PendingCodes> findByUserIdAndDeletedAtIsNull(String userId);
}
