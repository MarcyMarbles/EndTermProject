package kz.marcy.endtermproject.Entity.Transient;

public class SalaryDTO{
    private String salary;
    private int salaryType; // parse to enum
    private String currency;
    private String bankId; // nullable - ID банка, к которой привязана зарплата.
}
