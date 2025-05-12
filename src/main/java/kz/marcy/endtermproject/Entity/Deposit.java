package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class Deposit extends AbstractSuperClass {
    // Связь будет в виде вложенного объекта в классе Bank
    private String name; // Название депозита
    private BigDecimal amount = new BigDecimal(0); // Сумма депозита
    private BigDecimal interestRate = new BigDecimal(1); // Процентная ставка
    private String currency; // Валюта депозита
    private boolean isActive; // Активен ли депозит
}

