package kz.marcy.endtermproject.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document
@Data
public class PersonBalanceDetails extends AbstractSuperClass {
    private String salary;
    private SalaryType salaryType;
    private String currency;
}

