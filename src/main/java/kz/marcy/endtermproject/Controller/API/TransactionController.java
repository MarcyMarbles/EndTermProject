package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Transactions;
import kz.marcy.endtermproject.Service.TransactionService;
import kz.marcy.endtermproject.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    /** Все транзакции пользователя */
    @GetMapping("/all")
    public Flux<Transactions> getAllTransactions() {
        return getCurrentUserId()
                .flatMapMany(transactionService::findByUserId);
    }

    /** Только входящие (income) */
    @GetMapping("/deposit")
    public Flux<Transactions> getDepositTransactions() {
        return getCurrentUserId()
                .flatMapMany(id -> transactionService.findByUserIdAndType(id, "income"));
    }

    /** Только исходящие (expense) */
    @GetMapping("/withdraw")
    public Flux<Transactions> getWithdrawTransactions() {
        return getCurrentUserId()
                .flatMapMany(id -> transactionService.findByUserIdAndType(id, "expense"));
    }
}
