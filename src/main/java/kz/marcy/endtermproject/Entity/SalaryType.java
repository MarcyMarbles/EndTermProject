package kz.marcy.endtermproject.Entity;

import lombok.Getter;

@Getter
public enum SalaryType {
    DAILY("DAILY"),
    WEEKLY("WEEKLY"),
    BIWEEKLY("BIWEEKLY"),
    MONTHLY("MONTHLY");

    private final String value;

    SalaryType(String value) {
        this.value = value;
    }

    public static SalaryType fromValue(String value) {
        for (SalaryType type : SalaryType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown salary type: " + value);
    }
}

