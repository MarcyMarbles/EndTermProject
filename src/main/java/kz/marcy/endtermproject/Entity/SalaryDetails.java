package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class SalaryDetails extends AbstractSuperClass {
    private String ownerId; // ID владельца
    private String salary;
    private SalaryType salaryType;
    private String currency;
    private String bankId; // nullable - ID банка, к которой привязана зарплата.
}

