package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class Deposit extends AbstractSuperClass{
    // Связь будет в виде вложенного объекта в классе Bank
    private String name; // Название депозита
    private BigDecimal amount; // Сумма депозита
    private BigDecimal interestRate; // Процентная ставка
    private String currency; // Валюта депозита
    private boolean isActive; // Активен ли депозит
}
