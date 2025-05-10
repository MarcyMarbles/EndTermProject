package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Bank extends AbstractSuperClass{
    private String name;
    private String ownerId; // ID юзера, который создал банк
    private BigDecimal balance; // Баланс юзера
    private String currency; // Валюта банка
    private List<Deposit> deposit = new ArrayList<>(); // Депозиты юзера
}
