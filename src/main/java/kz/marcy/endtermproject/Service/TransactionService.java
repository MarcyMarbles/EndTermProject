package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Transactions;
import kz.marcy.endtermproject.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService extends AbstractSuperService<Transactions> {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Mono<Transactions> saveEntity(Transactions entity) {
        return transactionRepository.save(entity);
    }

    public Flux<Transactions> findByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }


}
