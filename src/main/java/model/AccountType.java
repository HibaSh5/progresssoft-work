package model;

import static org.apache.commons.validator.routines.IBANValidator.DEFAULT_IBAN_VALIDATOR;

public enum AccountType {
    IBAN {
        @Override
        public void validate(String value) {
            if (!DEFAULT_IBAN_VALIDATOR.isValid(value)) {
                throw new IllegalArgumentException("Invalid IBAN for " + value);
            }
        }
    },
    Mobile {
        @Override
        public void validate(String value) {
            if (value == null ||
            value.length() != 14 ||
            !value.startsWith("009627") ||
            !value.substring(7).chars().allMatch(Character::isDigit)) {
                throw new IllegalArgumentException("Invalid Mobile for " + value);
            }
        }
    },
    Alias {
        @Override
        public void validate(String value) {
            if (value == null ||
             value.isEmpty() || value.length() > 20 ||
             !Character.isLetter(value.charAt(0)) ||
             !value.chars().allMatch(Character::isLetterOrDigit)) {
                throw new IllegalArgumentException("Invalid Alias for " + value);
            }

        }
    };

    public static AccountType parse(String beneficiaryID) {
        return switch (beneficiaryID) {
            case "1" -> AccountType.IBAN;
            case "2" -> AccountType.Mobile;
            case "3" -> AccountType.Alias;
            default -> throw new IllegalArgumentException("Invalid beneficiary type.");
        };
    }

    public abstract void validate(String value);
}
