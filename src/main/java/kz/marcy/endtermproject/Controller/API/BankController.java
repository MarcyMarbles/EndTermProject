package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Bank;
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
        return bankService.getBankById(id)
                .flatMap(bank -> getCurrentUserId()
                        .filter(userId -> userId.equals(bank.getOwnerId()))
                        .flatMap(ignored -> bankService.subtractBalance(id, amount).map(ResponseEntity::ok))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
