package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Bank;
import kz.marcy.endtermproject.Entity.Deposit;
import kz.marcy.endtermproject.Entity.Transactions;
import kz.marcy.endtermproject.Service.BankService;
import kz.marcy.endtermproject.Service.TransactionService;
import kz.marcy.endtermproject.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/banks")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;
    private final UserService userService;
    private final TransactionService transactionService;

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userService::getUserByLogin)
                .map(AbstractSuperClass::getId);
    }

    @GetMapping
    public Flux<Bank> getUserBanks() {
        return getCurrentUserId()
                .flatMapMany(bankService::getBanksByOwnerId);
    }

    @PostMapping
    public Mono<ResponseEntity<Bank>> createBank(@RequestBody Bank bank) {
        return getCurrentUserId()
                .flatMap(userId -> {
                    bank.setOwnerId(userId);
                    return bankService.saveEntity(bank)
                            .map(ResponseEntity::ok);
                })
                .defaultIfEmpty(ResponseEntity.status(409).build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<?>> getBank(@PathVariable String id) {
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            return Mono.just(ResponseEntity.ok(bank));
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteBank(@PathVariable String id) {
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            return bankService.deleteBankById(id)
                                    .thenReturn(ResponseEntity.noContent().build());
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/add-balance")
    public Mono<ResponseEntity<?>> addBalance(@PathVariable String id,
                                                 @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            return bankService.addBalance(id, amount)
                                    .flatMap(updatedBank -> {
                                        Transactions transaction = new Transactions();
                                        transaction.setUserId(userId);
                                        transaction.setBankId(updatedBank.getId());
                                        transaction.setAmount(amount);
                                        transaction.setTransactionType("income");
                                        transaction.setCurrency(updatedBank.getCurrency());
                                        return transactionService.saveEntity(transaction)
                                                .thenReturn(ResponseEntity.ok(updatedBank));
                                    });
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/subtract-balance")
    public Mono<ResponseEntity<?>> subtractBalance(@PathVariable String id,
                                                      @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            if (bank.getBalance().compareTo(amount) < 0) {
                                return Mono.just(ResponseEntity.badRequest().build());
                            }
                            return bankService.subtractBalance(id, amount)
                                    .flatMap(updatedBank -> {
                                        Transactions transaction = new Transactions();
                                        transaction.setUserId(userId);
                                        transaction.setBankId(updatedBank.getId());
                                        transaction.setAmount(amount);
                                        transaction.setTransactionType("expense");
                                        transaction.setCurrency(updatedBank.getCurrency());
                                        return transactionService.saveEntity(transaction)
                                                .thenReturn(ResponseEntity.ok(updatedBank));
                                    });
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/add-deposit")
    public Mono<ResponseEntity<?>> addDeposit(@PathVariable String id,
                                                    @RequestBody Deposit deposit) {
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            return bankService.addNewDeposit(id, deposit)
                                    .flatMap(createdDeposit -> {
                                        Transactions transaction = new Transactions();
                                        transaction.setUserId(userId);
                                        transaction.setBankId(bank.getId());
                                        transaction.setDepositId(createdDeposit.getId());
                                        transaction.setAmount(createdDeposit.getAmount());
                                        transaction.setTransactionType("deposit_create");
                                        transaction.setCurrency(createdDeposit.getCurrency());
                                        return transactionService.saveEntity(transaction)
                                                .thenReturn(ResponseEntity.ok(createdDeposit));
                                    });
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}/delete-deposit/{depositId}")
    public Mono<ResponseEntity<Object>> deleteDeposit(@PathVariable String id,
                                                    @PathVariable String depositId) {
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            return bankService.removeDeposit(bank.getId(), depositId)
                                    .thenReturn(ResponseEntity.noContent().build());
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/deposits")
    public Flux<Deposit> getDeposits(@PathVariable String id) {
        return getCurrentUserId()
                .flatMapMany(userId -> bankService.getBankById(id)
                        .filter(bank -> userId.equals(bank.getOwnerId()))
                        .flatMapMany(bank -> Flux.fromIterable(bank.getDeposit()))
                );
    }

    @GetMapping("/{id}/deposits/{depositId}")
    public Mono<ResponseEntity<?>> getDeposit(@PathVariable String id,
                                                    @PathVariable String depositId) {
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            return bankService.getDepositById(bank.getId(), depositId)
                                    .map(ResponseEntity::ok)
                                    .defaultIfEmpty(ResponseEntity.notFound().build());
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/deposits/add-balance/{depositId}")
    public Mono<ResponseEntity<?>> addDepositBalance(@PathVariable String id,
                                                           @PathVariable String depositId,
                                                           @RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return getCurrentUserId()
                .flatMap(userId -> bankService.getBankById(id)
                        .flatMap(bank -> {
                            if (!userId.equals(bank.getOwnerId())) {
                                return Mono.just(ResponseEntity.status(403).build());
                            }
                            return bankService.addBalanceToDeposit(id, depositId, amount)
                                    .flatMap(updatedDeposit -> {
                                        Transactions transaction = new Transactions();
                                        transaction.setUserId(userId);
                                        transaction.setBankId(bank.getId());
                                        transaction.setDepositId(updatedDeposit.getId());
                                        transaction.setAmount(amount);
                                        transaction.setTransactionType("deposit_income");
                                        transaction.setCurrency(updatedDeposit.getCurrency());
                                        return transactionService.saveEntity(transaction)
                                                .thenReturn(ResponseEntity.ok(updatedDeposit));
                                    });
                        })
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
