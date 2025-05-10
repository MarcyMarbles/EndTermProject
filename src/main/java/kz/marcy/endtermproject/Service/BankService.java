package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Bank;
import kz.marcy.endtermproject.Entity.Deposit;
import kz.marcy.endtermproject.Repository.BankRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class BankService extends AbstractSuperService<Bank> {

    @Autowired
    private BankRepo bankRepo;

    @Override
    public Mono<Bank> saveEntity(Bank entity) {
        return bankRepo.save(entity);
    }

    public Flux<Bank> getAllBanks() {
        return bankRepo.findAll(); // Get all banks
    }

    public Flux<Bank> getBanksByOwnerId(String ownerId) {
        return bankRepo.findByOwnerId(ownerId); // Get banks by owner ID
    }

    public Flux<Bank> getBanksByName(String name) {
        return bankRepo.findByName(name); // Get banks by name
    }

    public Flux<Bank> getBanksByOwnerIdAndName(String ownerId, String name) {
        return bankRepo.findByOwnerIdAndName(ownerId, name); // Get banks by owner ID and name
    }

    public Flux<Bank> getBanksByBalanceAfter(BigDecimal balanceAfter) {
        return bankRepo.findByBalanceAfter(balanceAfter); // Get banks by balance after
    }

    public Mono<Bank> getBankById(String id) {
        return bankRepo.findById(id); // Get bank by ID
    }

    public Mono<Void> deleteBankById(String id) {
        return bankRepo.findById(id)
                .flatMap(this::softDelete)
                .then();
    }

    public Mono<Bank> addBalance(String bankId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("Amount must be positive"));
        }
        return bankRepo.findById(bankId)
                .flatMap(bank -> {
                    bank.setBalance(bank.getBalance().add(amount));
                    return saveEntity(bank);
                });
    }

    public Mono<Bank> subtractBalance(String bankId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("Amount must be positive"));
        }
        return bankRepo.findById(bankId)
                .flatMap(bank -> {
                    bank.setBalance(bank.getBalance().subtract(amount));
                    return saveEntity(bank);
                });
    }

    public Mono<Deposit> addNewDeposit(String bankId, Deposit deposit) {
        return bankRepo.findById(bankId)
                .flatMap(bank -> {
                    bank.getDeposit().add(deposit);
                    return saveEntity(bank)
                            .then(Mono.just(deposit));
                });
    }

    public Mono<Bank> removeDeposit(String bankId, String depositId) {
        return bankRepo.findById(bankId)
                .flatMap(bank -> {
                    Deposit deposit = findDepositById(bank, depositId);
                    if (deposit != null) {
                        deposit.setDeletedAt(Instant.now()); // Закрыть депозит
                        return saveEntity(bank);
                    } else {
                        return Mono.empty();
                    }
                });
    }

    public Mono<Deposit> addBalanceToDeposit(String bankId, String depositId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("Amount must be positive"));
        }
        return bankRepo.findById(bankId)
                .flatMap(bank -> {
                    Deposit deposit = findDepositById(bank, depositId);
                    if (deposit != null) {
                        deposit.setAmount(deposit.getAmount().add(amount));
                        return saveEntity(bank)
                                .then(Mono.just(deposit));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    public Mono<Deposit> subtractBalanceFromDeposit(String bankId, String depositId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.error(new IllegalArgumentException("Amount must be positive"));
        }
        return bankRepo.findById(bankId)
                .flatMap(bank -> {
                    Deposit deposit = findDepositById(bank, depositId);
                    if (deposit != null) {
                        deposit.setAmount(deposit.getAmount().subtract(amount));
                        return saveEntity(bank)
                                .then(Mono.just(deposit));
                    } else {
                        return Mono.empty();
                    }
                });
    }

    public Flux<Deposit> getDepositsByBankId(String bankId) {
        return bankRepo.findById(bankId)
                .flatMapMany(bank -> Flux.fromIterable(bank.getDeposit())
                        .filter(deposit -> deposit.getDeletedAt() == null)); // Get deposits by bank ID
    }

    public Mono<Deposit> getDepositById(String bankId, String depositId) {
        return bankRepo.findById(bankId)
                .flatMap(bank -> {
                    Deposit deposit = findDepositById(bank, depositId);
                    if (deposit != null) {
                        return Mono.just(deposit);
                    } else {
                        return Mono.empty();
                    }
                });
    }

    public Flux<Bank> getBanksByBalanceAfterAndOwnerId(BigDecimal balanceAfter, String ownerId) {
        return bankRepo.findByBalanceAfter(balanceAfter)
                .filter(bank -> bank.getOwnerId().equals(ownerId)); // Get banks by balance after and owner ID
    }

    public Flux<Bank> getBanksWithDepositBalanceGreaterThan(BigDecimal balance) {
        return bankRepo.findByDepositAmountGreaterThan(balance);
    }

    public Flux<Bank> getBanksWithDepositBalanceLessThan(BigDecimal balance) {
        return bankRepo.findByDepositAmountLessThan(balance);
    }

    public Flux<Bank> getBanksWithDepositBalanceBetween(BigDecimal min, BigDecimal max) {
        return bankRepo.findByDepositAmountBetween(min, max);
    }

    public Flux<Bank> getBanksWithDepositBalanceEqualTo(BigDecimal balance) {
        return bankRepo.findByDepositAmountEquals(balance);
    }

    public Flux<Bank> getBanksByOwnerIdAndDepositBalanceGreaterThan(String ownerId, BigDecimal balance) {
        return bankRepo.findByOwnerIdAndDepositAmountGreaterThan(ownerId, balance);
    }

    public Flux<Bank> getBanksByOwnerIdAndDepositBalanceLessThan(String ownerId, BigDecimal balance) {
        return bankRepo.findByOwnerIdAndDepositAmountLessThan(ownerId, balance);
    }

    public Flux<Bank> getBanksByOwnerIdAndDepositBalanceBetween(String ownerId, BigDecimal min, BigDecimal max) {
        return bankRepo.findByOwnerIdAndDepositAmountBetween(ownerId, min, max);
    }

    public Flux<Bank> getBanksByOwnerIdAndDepositBalanceEqualTo(String ownerId, BigDecimal balance) {
        return bankRepo.findByOwnerIdAndDepositAmountEquals(ownerId, balance);
    }


    private Deposit findDepositById(Bank bank, String depositId) {
        return bank.getDeposit().stream()
                .filter(d -> d.getId().equals(depositId) && d.getDeletedAt() == null)
                .findFirst()
                .orElse(null);
    }


}
