package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@Document
public class Balance extends AbstractSuperClass {
    private String ownerId; // ID юзера, который создал банк
    private BigDecimal balance = new BigDecimal(0); // Баланс, чтобы не было null
    private String currency; // Валюта
}

