package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.SalaryDetails;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface SalaryDetailsRepo extends ReactiveMongoRepository<SalaryDetails, String> {
    Mono<SalaryDetails> findByIdAndDeletedAtIsNull(String id);

    Mono<SalaryDetails> findByOwnerIdAndDeletedAtIsNull(String ownerId);
} 