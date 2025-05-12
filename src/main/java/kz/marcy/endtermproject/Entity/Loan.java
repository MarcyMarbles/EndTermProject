package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "loans")
@Data
public class Loan extends AbstractSuperClass {

    private String loanerId;         // ID пользователя, связанного с займом
    private String loanerName;       // Имя того, у кого взяли или кому дали

    private LoanType loanType;       // GIVE или TAKE

    private BigDecimal amount;       // 💰 Лучше использовать BigDecimal для работы с деньгами
    private String currency;         // Например, "KZT", "USD"

    private String description;      // Комментарий или цель займа

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate date;          // 📅 Дата займа

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate approximateDate; // 📅 Примерная дата возврата
}

