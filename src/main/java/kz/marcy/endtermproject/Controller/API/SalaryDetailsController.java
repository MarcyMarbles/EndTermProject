package kz.marcy.endtermproject.Controller.API;

import kz.marcy.endtermproject.Entity.AbstractSuperClass;
import kz.marcy.endtermproject.Entity.SalaryDetails;
import kz.marcy.endtermproject.Entity.SalaryType;
import kz.marcy.endtermproject.Service.SalaryDetailsService;
import kz.marcy.endtermproject.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@RestController
@RequestMapping("/api/salary-details")
@RequiredArgsConstructor
public class SalaryDetailsController {

    private final SalaryDetailsService salaryDetailsService;
    private final UserService userService;

    private Mono<String> getCurrentUserId() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication().getName())
                .flatMap(userService::getUserByLogin)
                .map(AbstractSuperClass::getId);
    }

    @PostMapping
    public Mono<ResponseEntity<SalaryDetails>> createPersonBalanceDetails(@RequestBody SalaryDetails details) {
        return getCurrentUserId()
                .flatMap(userId -> {
                    details.setOwnerId(userId); // Using userId as the ID for one-to-one relationship
                    return salaryDetailsService.saveEntity(details)
                            .map(ResponseEntity::ok);
                });
    }

    @GetMapping
    public Mono<ResponseEntity<SalaryDetails>> getMyPersonBalanceDetails() {
        return getCurrentUserId()
                .flatMap(salaryDetailsService::getPersonBalanceDetailsById)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping
    public Mono<ResponseEntity<SalaryDetails>> updatePersonBalanceDetails(@RequestBody SalaryDetails details) {
        return getCurrentUserId()
                .flatMap(userId -> {
                    details.setId(userId);
                    return salaryDetailsService.saveEntity(details)
                            .map(ResponseEntity::ok);
                });
    }

    @DeleteMapping
    public Mono<ResponseEntity<Object>> deletePersonBalanceDetails() {
        return getCurrentUserId()
                .flatMap(salaryDetailsService::deletePersonBalanceDetailsById)
                .thenReturn(ResponseEntity.noContent().build());
    }

    @GetMapping("/get-salary-types")
    public Mono<ResponseEntity<String[]>> getSalaryTypes() {
        return Mono.just(ResponseEntity.ok(Arrays.stream(SalaryType.values())
                .map(SalaryType::name)
                .toArray(String[]::new)));
    }
} 