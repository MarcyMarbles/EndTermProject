package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Loan;
import kz.marcy.endtermproject.Entity.LoanType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface LoanRepo extends ReactiveMongoRepository<Loan, String> {
    Flux<Loan> findByLoanerId(String loanerId);
    Flux<Loan> findByLoanerIdAndLoanType(String loanerId, LoanType loanType);
} 