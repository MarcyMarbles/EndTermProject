package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Transactions;
import kz.marcy.endtermproject.Repository.TransactionRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TransactionService extends AbstractSuperService<Transactions> {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Mono<Transactions> saveEntity(Transactions entity) {
        return transactionRepository.save(entity);
    }

    /**
     * Возвращает все транзакции по given userId
     */
    public Flux<Transactions> findByUserId(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    /**
     * Возвращает транзакции указанного типа ("income" или "expense") для данного userId
     */
    public Flux<Transactions> findByUserIdAndType(String userId, String transactionType) {
        return transactionRepository.findByUserIdAndTransactionType(userId, transactionType);
    }
}
