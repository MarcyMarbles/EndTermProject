package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.SalaryDetails;
import kz.marcy.endtermproject.Repository.SalaryDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class SalaryDetailsService extends AbstractSuperService<SalaryDetails> {

    @Autowired
    private SalaryDetailsRepo salaryDetailsRepo;

    @Override
    public Mono<SalaryDetails> saveEntity(SalaryDetails entity) {
        return salaryDetailsRepo.save(entity);
    }

    public Mono<SalaryDetails> getPersonBalanceDetailsById(String id) {
        return salaryDetailsRepo.findByIdAndDeletedAtIsNull(id);
    }

    public Mono<Void> deletePersonBalanceDetailsById(String id) {
        return salaryDetailsRepo.findById(id)
                .flatMap(this::softDelete)
                .then();
    }

    public Mono<SalaryDetails> getPersonBalanceDetailsByOwnerId(String ownerId) {
        return salaryDetailsRepo.findByOwnerIdAndDeletedAtIsNull(ownerId);
    }
} 