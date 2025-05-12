package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Transactions;
import kz.marcy.endtermproject.Service.TransactionService;
import kz.marcy.endtermproject.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userService::getUserByLogin)
                .map(AbstractSuperClass::getId);
    }

    /**
     * Все транзакции пользователя
     */
    @GetMapping("/all")
    public Mono<ResponseEntity<List<Transactions>>> getAllTransactions() {
        return getCurrentUserId()
                .flatMap(userId ->
                        transactionService.findByUserId(userId)
                                .collectList()
                                .map(list ->
                                        list.isEmpty()
                                                ? ResponseEntity.notFound().build()
                                                : ResponseEntity.ok(list)
                                )
                );
    }

    /**
     * Только входящие (income)
     */
    @GetMapping("/deposit")
    public Mono<ResponseEntity<List<Transactions>>> getDepositTransactions() {
        return getCurrentUserId()
                .flatMap(userId ->
                        transactionService.findByUserIdAndType(userId, "income")
                                .collectList()
                                .map(list ->
                                        list.isEmpty()
                                                ? ResponseEntity.notFound().build()
                                                : ResponseEntity.ok(list)
                                )
                );
    }

    /**
     * Только исходящие (expense)
     */
    @GetMapping("/withdraw")
    public Mono<ResponseEntity<List<Transactions>>> getWithdrawTransactions() {
        return getCurrentUserId()
                .flatMap(userId ->
                        transactionService.findByUserIdAndType(userId, "expense")
                                .collectList()
                                .map(list ->
                                        list.isEmpty()
                                                ? ResponseEntity.notFound().build()
                                                : ResponseEntity.ok(list)
                                )
                );
    }
}
