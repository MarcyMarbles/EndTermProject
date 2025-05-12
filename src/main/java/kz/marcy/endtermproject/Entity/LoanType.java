package kz.marcy.endtermproject.Entity;

import lombok.Getter;

import java.util.List;

@Getter
public enum LoanType {
    GIVE(List.of("Gave to", "Одолжил")),
    TAKE(List.of("Took from", "Занял у"));

    private final List<String> aliases;

    LoanType(List<String> aliases) {
        this.aliases = aliases;
    }

    public static LoanType fromValue(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan type input cannot be null or empty");
        }

        String lowerInput = input.toLowerCase();

        for (LoanType type : LoanType.values()) {
            for (String alias : type.aliases) {
                if (alias.equalsIgnoreCase(input)) {
                    return type;
                }
            }
        }

        for (LoanType type : LoanType.values()) {
            for (String alias : type.aliases) {
                if (alias.toLowerCase().contains(lowerInput)) {
                    return type;
                }
            }
        }

        throw new IllegalArgumentException("Unknown loan type: " + input);
    }

    public String getLabel() {
        return aliases.get(1); // или выбрать по языку позже
    }

}

