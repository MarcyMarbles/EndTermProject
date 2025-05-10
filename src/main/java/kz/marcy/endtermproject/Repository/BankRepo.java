package kz.marcy.endtermproject.Repository;

import kz.marcy.endtermproject.Entity.Bank;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

public interface BankRepo extends ReactiveMongoRepository<Bank, String> {
    Flux<Bank> findByOwnerId(String ownerId); // Метод для поиска банка по ID владельца

    Flux<Bank> findByName(String name); // Метод для поиска банка по имени

    Flux<Bank> findByOwnerIdAndName(String ownerId, String name); // Метод для поиска банка по ID владельца и имени

    Flux<Bank> findByBalanceAfter(BigDecimal balanceAfter);


    @Query("{ 'deposit.amount': { $gt: ?0 } }")
    Flux<Bank> findByDepositAmountGreaterThan(BigDecimal amount);

    @Query("{ 'deposit.amount': { $lt: ?0 } }")
    Flux<Bank> findByDepositAmountLessThan(BigDecimal amount);

    @Query("{ 'deposit.amount': { $gte: ?0, $lte: ?1 } }")
    Flux<Bank> findByDepositAmountBetween(BigDecimal min, BigDecimal max);

    @Query("{ 'deposit.amount': ?0 }")
    Flux<Bank> findByDepositAmountEquals(BigDecimal amount);

    @Query("{ 'ownerId': ?0, 'deposit.amount': { $gt: ?1 } }")
    Flux<Bank> findByOwnerIdAndDepositAmountGreaterThan(String ownerId, BigDecimal amount);

    @Query("{ 'ownerId': ?0, 'deposit.amount': { $lt: ?1 } }")
    Flux<Bank> findByOwnerIdAndDepositAmountLessThan(String ownerId, BigDecimal amount);

    @Query("{ 'ownerId': ?0, 'deposit.amount': { $gte: ?1, $lte: ?2 } }")
    Flux<Bank> findByOwnerIdAndDepositAmountBetween(String ownerId, BigDecimal min, BigDecimal max);

    @Query("{ 'ownerId': ?0, 'deposit.amount': ?1 }")
    Flux<Bank> findByOwnerIdAndDepositAmountEquals(String ownerId, BigDecimal amount);
}
