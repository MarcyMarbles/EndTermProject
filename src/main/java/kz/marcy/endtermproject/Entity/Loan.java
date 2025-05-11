package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class Loan extends AbstractSuperClass {
    private String loanerId; // UserID who took or gave the loan
    private String loanerName; // Кому дали или у кого взяли
    private LoanType loanType; // GIVE or TAKE
    private String amount; // Сумма займа
    private String currency; // Валюта займа
    private String description; // Описание займа
    private String date; // Дата займа
    private String approximateDate; // Ожидаемая дата возврата займа
}
