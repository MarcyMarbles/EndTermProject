package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.Balance;
import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Transactions;
import kz.marcy.endtermproject.Service.BalanceForecastDto;
import kz.marcy.endtermproject.Service.BalanceService;
import kz.marcy.endtermproject.Service.TransactionService;
import kz.marcy.endtermproject.Service.UserService;
import kz.marcy.endtermproject.Repository.BalanceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceRepo balanceRepo;
    private final UserService userService;
    private final BalanceService balanceService;
    private final TransactionService transactionService;

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userService::getUserByLogin)
                .map(AbstractSuperClass::getId);
    }

    @GetMapping
    public Mono<ResponseEntity<Balance>> getMyBalance() {
        return getCurrentUserId()
                .flatMap(ownerId ->
                        balanceRepo.findByOwnerId(ownerId)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build())
                );
    }

    @PostMapping
    public Mono<ResponseEntity<Balance>> createBalance(@RequestBody Balance balance) {
        return getCurrentUserId()
                .flatMap(ownerId ->
                        balanceRepo.findByOwnerId(ownerId)
                                // если баланс уже есть — 409 Conflict
                                .flatMap(existing ->
                                        Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).<Balance>build())
                                )
                                // если баланс не найден — сохраняем новый
                                .switchIfEmpty(
                                        Mono.defer(() -> {
                                            balance.setOwnerId(ownerId);
                                            return balanceRepo.save(balance)
                                                    .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
                                        })
                                )
                );
    }

    @PatchMapping("/add")
    public Mono<ResponseEntity<Balance>> addBalance(@RequestParam BigDecimal amount) {
        if (amount.signum() < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return getCurrentUserId()
                .flatMap(ownerId ->
                        balanceRepo.findByOwnerId(ownerId)
                                .flatMap(balance -> {
                                    Transactions tx = new Transactions();
                                    tx.setInitialAmount(balance.getBalance());
                                    balance.setBalance(balance.getBalance().add(amount));

                                    tx.setUserId(ownerId);
                                    tx.setBalanceId(balance.getId());
                                    tx.setAmount(amount);
                                    tx.setTransactionType("income");
                                    tx.setCurrency(balance.getCurrency());

                                    return transactionService.saveEntity(tx)
                                            .then(balanceRepo.save(balance))
                                            .map(ResponseEntity::ok);
                                })
                                .defaultIfEmpty(ResponseEntity.notFound().build())
                );
    }

    @PatchMapping("/subtract")
    public Mono<ResponseEntity<?>> subtractBalance(@RequestParam BigDecimal amount) {
        if (amount.signum() < 0) {
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return getCurrentUserId()
                .flatMap(ownerId ->
                        balanceRepo.findByOwnerId(ownerId)
                                .flatMap(balance -> {
                                    if (balance.getBalance().compareTo(amount) < 0) {
                                        return Mono.just(ResponseEntity.badRequest().build());
                                    }
                                    Transactions tx = new Transactions();
                                    tx.setInitialAmount(balance.getBalance());
                                    balance.setBalance(balance.getBalance().subtract(amount));

                                    tx.setUserId(ownerId);
                                    tx.setBalanceId(balance.getId());
                                    tx.setAmount(amount);
                                    tx.setTransactionType("expense");
                                    tx.setCurrency(balance.getCurrency());

                                    return transactionService.saveEntity(tx)
                                            .then(balanceRepo.save(balance))
                                            .map(ResponseEntity::ok);
                                })
                                .defaultIfEmpty(ResponseEntity.notFound().build())
                );
    }

    @DeleteMapping
    public Mono<ResponseEntity<Void>> deleteMyBalance() {
        return getCurrentUserId()
                .flatMap(ownerId ->
                        balanceRepo.findByOwnerId(ownerId)
                                .flatMap(balance ->
                                        balanceRepo.deleteById(balance.getId())
                                                .thenReturn(ResponseEntity.noContent().<Void>build())
                                )
                                .defaultIfEmpty(ResponseEntity.notFound().build())
                );
    }

    @GetMapping("/count-up-to/{date}")
    public Mono<ResponseEntity<BalanceForecastDto>> countUpToDate(@PathVariable String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        return getCurrentUserId()
                .flatMap(userId ->
                        balanceService.countToDate(userId, parsedDate)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build())
                );
    }
}
