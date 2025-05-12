package kz.marcy.endtermproject.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BalanceEvent(LocalDate date, String type, String description, BigDecimal amount) {
}
