package kz.marcy.endtermproject.Service;

import java.math.BigDecimal;
import java.util.List;

public record BalanceForecastDto(
        BigDecimal finalBalance,
        List<BalanceEvent> events,
        String currency
) {
}
