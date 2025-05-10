package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Bank;
import kz.marcy.endtermproject.Entity.Deposit;
import kz.marcy.endtermproject.Service.BankService;
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

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userService::getUserByLogin)
                .map(AbstractSuperClass::getId);
    }

    @GetMapping
    public Flux<Bank> getUserBanks() {
        return getCurrentUserId().flatMapMany(bankService::getBanksByOwnerId);
    }

    @PostMapping
    public Mono<Bank> createBank(@RequestBody Bank bank) {
        return getCurrentUserId()
                .flatMap(userId -> {
                    bank.setOwnerId(userId);
                    return bankService.saveEntity(bank);
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Bank>> getBank(@PathVariable String id) {
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .map(ignored -> ResponseEntity.ok(bank))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteBank(@PathVariable String id) {
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.deleteBankById(id).thenReturn(ResponseEntity.noContent().build()))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{id}/add-balance")
    public Mono<ResponseEntity<Bank>> addBalance(@PathVariable String id, @RequestParam BigDecimal amount) {
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.addBalance(id, amount).map(ResponseEntity::ok))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{id}/subtract-balance")
    public Mono<ResponseEntity<Bank>> subtractBalance(@PathVariable String id, @RequestParam BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.subtractBalance(id, amount).map(ResponseEntity::ok))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{id}/add-deposit")
    public Mono<ResponseEntity<Deposit>> addDeposit(@PathVariable String id, @RequestBody Deposit deposit) {
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.addNewDeposit(id, deposit).map(ResponseEntity::ok))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}/delete-deposit/{depositId}")
    public Mono<ResponseEntity<Object>> deleteDeposit(@PathVariable String id, @PathVariable String depositId) {
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.removeDeposit(bank.getId(), depositId).thenReturn(ResponseEntity.noContent().build()))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping("/{id}/deposits")
    public Flux<Deposit> getDeposits(@PathVariable String id) {
        return bankService.getBankById(id)
                .flatMapMany(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .map(ignored -> bank.getDeposit())
                        .flatMapMany(Flux::fromIterable))
                .switchIfEmpty(Flux.empty());
    }


    @GetMapping("/{id}/deposits/{depositId}")
    public Mono<ResponseEntity<Deposit>> getDeposit(@PathVariable String id, @PathVariable String depositId) {
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.getDepositById(id, depositId).map(ResponseEntity::ok))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/{id}/deposits/add-balance/{depositId}")
    public Mono<ResponseEntity<Deposit>> addDepositBalance(@PathVariable String id, @PathVariable String depositId, @RequestParam BigDecimal amount) {
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.addBalanceToDeposit(id, depositId, amount).map(ResponseEntity::ok))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
