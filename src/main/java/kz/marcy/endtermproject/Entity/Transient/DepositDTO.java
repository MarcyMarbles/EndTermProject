package kz.marcy.endtermproject.Entity.Transient;

import kz.marcy.endtermproject.Entity.Deposit;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DepositDTO{
    private String name; // Название депозита
    private BigDecimal amount; // Сумма депозита
    private BigDecimal interestRate; // Процентная ставка
    private String currency; // Валюта депозита

    public DepositDTO(Deposit deposit) {
        this.name = deposit.getName();
        this.amount = deposit.getAmount();
        this.interestRate = deposit.getInterestRate();
        this.currency = deposit.getCurrency();
    }
}
