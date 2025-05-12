package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Transactions;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransactionRepository extends ReactiveMongoRepository<Transactions, String> {
    Flux<Transactions> findByUserId(String userId);

    Flux<Transactions> findByBankId(String bankId);

    Flux<Transactions> findByBalanceId(String balanceId);

    Flux<Transactions> findByUserIdAndBankId(String userId, String bankId);

    Flux<Transactions> findByUserIdAndBalanceId(String userId, String balanceId);

    Flux<Transactions> findByBankIdAndBalanceId(String bankId, String balanceId);

    Flux<Transactions> findByUserIdAndBankIdAndBalanceId(String userId, String bankId, String balanceId);

    Flux<Transactions> findByTransactionType(String transactionType);

    Flux<Transactions> findByTransactionTypeAndUserId(String transactionType, String userId);
    Flux<Transactions> findByTransactionTypeAndBankId(String transactionType, String bankId);
    Flux<Transactions> findByTransactionTypeAndBalanceId(String transactionType, String balanceId);

}
