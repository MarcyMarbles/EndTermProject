package kz.marcy.endtermproject.Entity;

public enum LoanType {
    GIVE("GIVE"), // Дал кому либо
    TAKE("TAKE"); // Взял у кого либо

    private final String value;

    LoanType(String value) {
        this.value = value;
    }

    public static LoanType fromValue(String value) {
        for (LoanType type : LoanType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown loan type: " + value);
    }
    
}
