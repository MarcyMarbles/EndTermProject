package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Loan;
import kz.marcy.endtermproject.Entity.LoanType;
import kz.marcy.endtermproject.Repository.LoanRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Service
public class LoanService extends AbstractSuperService<Loan> {

    @Autowired
    private LoanRepo loanRepo;

    @Override
    public Mono<Loan> saveEntity(Loan entity) {
        return loanRepo.save(entity);
    }

    public Flux<Loan> getLoansByLoanerId(String loanerId) {
        return loanRepo.findByLoanerId(loanerId);
    }

    public Flux<Loan> getLoansByLoanerIdAndType(String loanerId, String loanType) {
        LoanType type = LoanType.fromValue(loanType);
        return loanRepo.findByLoanerIdAndLoanType(loanerId, type);
    }

    public Mono<Loan> getLoanById(String id) {
        return loanRepo.findById(id);
    }

    public Mono<Void> deleteLoanById(String id) {
        return loanRepo.findById(id)
                .flatMap(this::softDelete)
                .then();
    }
} 