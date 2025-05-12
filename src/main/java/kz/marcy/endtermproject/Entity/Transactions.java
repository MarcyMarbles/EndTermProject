package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Transactions extends AbstractSuperClass {
    private String bankId;
    private String balanceId;
    private String depositId;
    private String userId;
    private BigDecimal amount;
    private String currency;
    private String transactionType; // income, expense
    // Этот класс сделан для транзакций,
    // То есть история пополнений и списаний средств который предоставляет юзер
}
