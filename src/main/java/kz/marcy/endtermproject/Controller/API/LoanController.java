package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.Loan;
import kz.marcy.endtermproject.Entity.LoanType;
import kz.marcy.endtermproject.Service.LoanService;
import kz.marcy.endtermproject.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userService::getUserByLogin)
                .map(AbstractSuperClass::getId);
    }

    @GetMapping
    public Flux<Loan> getMyLoans() {
        return getCurrentUserId()
                .flatMapMany(loanService::getLoansByLoanerId);
    }

    @GetMapping("/type/{loanType}")
    public Flux<Loan> getMyLoansByType(@PathVariable String loanType) {
        return getCurrentUserId()
                .flatMapMany(userId -> loanService.getLoansByLoanerIdAndType(userId, loanType));
    }

    @PostMapping
    public Mono<ResponseEntity<Loan>> createLoan(@RequestBody Loan loan) {
        return getCurrentUserId()
                .flatMap(userId -> {
                    loan.setLoanerId(userId);
                    return loanService.saveEntity(loan)
                            .map(ResponseEntity::ok);
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Loan>> getLoan(@PathVariable String id) {
        return loanService.getLoanById(id)
                .flatMap(loan -> getCurrentUserId()
                        .filter(userId -> userId.equals(loan.getLoanerId()))
                        .map(ignored -> ResponseEntity.ok(loan))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> deleteLoan(@PathVariable String id) {
        return loanService.getLoanById(id)
                .flatMap(loan -> getCurrentUserId()
                        .filter(userId -> userId.equals(loan.getLoanerId()))
                        .flatMap(ignored -> loanService.deleteLoanById(id)
                                .thenReturn(ResponseEntity.noContent().build()))
                        .switchIfEmpty(Mono.just(ResponseEntity.status(403).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    public record LoanTypeDto(String code, String label) {}

    @GetMapping("/loan-types")
    public Mono<List<LoanTypeDto>> getLoanTypes() {
        return Mono.just(
                Arrays.stream(LoanType.values())
                        .map(type -> new LoanTypeDto(type.name(), type.getLabel()))
                        .toList()
        );
    }

} 