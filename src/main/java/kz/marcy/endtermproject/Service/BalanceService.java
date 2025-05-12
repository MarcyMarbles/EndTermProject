package kz.marcy.endtermproject.Service;

import kz.marcy.endtermproject.Entity.Balance;
import kz.marcy.endtermproject.Entity.LoanType;
import kz.marcy.endtermproject.Entity.SalaryType;
import kz.marcy.endtermproject.Repository.BalanceRepo;
import kz.marcy.endtermproject.Repository.LoanRepo;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BalanceService extends AbstractSuperService<Balance> {
    private final BalanceRepo balanceRepo;

    private final LoanService loanService;

    private final SalaryDetailsService salaryDetailsService;

    private final LoanRepo loanRepo;

    public BalanceService(BalanceRepo balanceRepo, LoanService loanService, SalaryDetailsService salaryDetailsService, LoanRepo loanRepo) {
        this.balanceRepo = balanceRepo;
        this.loanService = loanService;
        this.salaryDetailsService = salaryDetailsService;
        this.loanRepo = loanRepo;
    }


    @Override
    public Mono<Balance> saveEntity(Balance entity) {
        return balanceRepo.save(entity);
    }

    public Mono<BalanceForecastDto> countToDate(String userId, LocalDate toDate) {
        LocalDate today = LocalDate.now();

        Mono<Balance> baseBalanceMono = balanceRepo.findByOwnerId(userId); // Дефолтный баланс

        Mono<List<BalanceEvent>> salaryEventsMono = salaryDetailsService.getPersonBalanceDetailsByOwnerId(userId)
                .map(details -> {
                    BigDecimal salaryPerPeriod = new BigDecimal(details.getSalary());
                    SalaryType type = details.getSalaryType();
                    String description = type.name() + " salary";

                    List<BalanceEvent> events = new ArrayList<>();
                    LocalDate date = today;

                    while (!date.isAfter(toDate)) {
                        events.add(new BalanceEvent(date, "SALARY", description, salaryPerPeriod));
                        date = switch (type) {
                            case DAILY -> date.plusDays(1);
                            case WEEKLY -> date.plusWeeks(1);
                            case BIWEEKLY -> date.plusWeeks(2);
                            case MONTHLY -> date.plusMonths(1);
                        };
                    }

                    return events;
                })
                .defaultIfEmpty(List.of());

        Mono<List<BalanceEvent>> loanEventsMono = loanRepo.findByLoanerId(userId)
                .filter(loan -> loan.getDeletedAt() == null)
                .filter(loan -> loan.getApproximateDate() != null
                        && !loan.getApproximateDate().isBefore(today)
                        && !loan.getApproximateDate().isAfter(toDate))
                .map(loan -> {
                    BigDecimal amount = loan.getLoanType() == LoanType.GIVE
                            ? loan.getAmount()
                            : loan.getAmount().negate();
                    String type = "LOAN_" + loan.getLoanType().name();
                    return new BalanceEvent(loan.getApproximateDate(), type, loan.getDescription(), amount);
                })
                .collectList();

        return Mono.zip(baseBalanceMono, salaryEventsMono, loanEventsMono)
                .map(tuple -> {
                    Balance baseBalance = tuple.getT1();
                    List<BalanceEvent> salaryEvents = tuple.getT2();
                    List<BalanceEvent> loanEvents = tuple.getT3();

                    List<BalanceEvent> allEvents = new ArrayList<>();
                    allEvents.addAll(salaryEvents);
                    allEvents.addAll(loanEvents);
                    allEvents.sort(Comparator.comparing(BalanceEvent::date));

                    BigDecimal finalBalance = allEvents.stream()
                            .map(BalanceEvent::amount)
                            .reduce(baseBalance.getBalance(), BigDecimal::add);

                    return new BalanceForecastDto(
                            finalBalance,
                            allEvents,
                            baseBalance.getCurrency()
                    );
                });
    }


}

