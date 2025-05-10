package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.Balance;
import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Service.UserService;
import kz.marcy.endtermproject.Repository.BalanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceRepo balanceRepo;
    private final UserService userService;

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userService::getUserByLogin)
                .map(AbstractSuperClass::getId);
    }

    @GetMapping
    public Mono<ResponseEntity<Balance>> getMyBalance() {
        return getCurrentUserId()
                .flatMap(ownerId -> balanceRepo.findByOwnerId(ownerId).next()
                        .map(ResponseEntity::ok)
                        .defaultIfEmpty(ResponseEntity.notFound().build()));
    }

    @PostMapping
    public Mono<ResponseEntity<Balance>> createBalance(@RequestBody Balance balance) {
        return getCurrentUserId()
                .flatMap(ownerId -> balanceRepo.findByOwnerId(ownerId).hasElements()
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.just(ResponseEntity.status(409).body(null)); // 409 Conflict
                            }
                            balance.setOwnerId(ownerId);
                            return balanceRepo.save(balance).map(ResponseEntity::ok);
                        }));
    }

    @PatchMapping("/add")
    public Mono<ResponseEntity<Balance>> addBalance(@RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return getCurrentUserId()
                .flatMap(ownerId -> balanceRepo.findByOwnerId(ownerId).next()
                        .flatMap(balance -> {
                            balance.setBalance(balance.getBalance().add(amount));
                            return balanceRepo.save(balance).map(ResponseEntity::ok);
                        })
                        .defaultIfEmpty(ResponseEntity.notFound().build()));
    }

    @PatchMapping("/subtract")
    public Mono<ResponseEntity<?>> subtractBalance(@RequestParam BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return getCurrentUserId()
                .flatMap(ownerId -> balanceRepo.findByOwnerId(ownerId).next()
                        .flatMap(balance -> {
                            if (balance.getBalance().compareTo(amount) < 0) {
                                return Mono.just(ResponseEntity.badRequest()
                                        .body(null)); // или вернуть кастомное сообщение
                            }
                            balance.setBalance(balance.getBalance().subtract(amount));
                            return balanceRepo.save(balance).map(ResponseEntity::ok);
                        })
                        .defaultIfEmpty(ResponseEntity.notFound().build()));
    }

    @DeleteMapping
    public Mono<ResponseEntity<Object>> deleteMyBalance() {
        return getCurrentUserId()
                .flatMap(ownerId -> balanceRepo.findByOwnerId(ownerId).next()
                        .flatMap(balance -> balanceRepo.deleteById(balance.getId())
                                .thenReturn(ResponseEntity.noContent().build()))
                        .defaultIfEmpty(ResponseEntity.notFound().build()));
    }
}
