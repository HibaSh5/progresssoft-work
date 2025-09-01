package util;

public class Validator {


    private static boolean isAlphanumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    // IBAN
    public static boolean isValidIban(String iban) {
        if (iban == null || iban.length() != 30) {
            return false;
        }

        // Must start with "JO"
        if (!iban.startsWith("JO")) {
            return false;
        }

        // Check that characters 3 and 4 are digits
        String checkDigits = iban.substring(2, 4);
        if (!isNumeric(checkDigits)) {
            return false;
        }

        // Check that characters 5–30 are alphanumeric
        String bban = iban.substring(4);
        return isAlphanumeric(bban);
    }


    // MOBILE
    public static boolean isValidMobile(String mobile) {
        if (mobile == null || mobile.length() != 14) {
            return false;
        }

        // Must start with "009627"
        if (!mobile.startsWith("009627")) {
            return false;
        }

        // Last 7 characters must be digits
        String numberPart = mobile.substring(7);
        return isNumeric(numberPart);
    }

    // ALIAS
    public static boolean isValidAlias(String alias) {
        if (alias == null || alias.isEmpty() || alias.length() > 20) {
            return false;
        }

        // First character must be a letter
        if (!Character.isLetter(alias.charAt(0))) {
            return false;
        }

        // Remaining characters must be alphanumeric
        for (int i = 1; i < alias.length(); i++) {
            char c = alias.charAt(i);
            if (!Character.isLetterOrDigit(c)) {
                return false;
            }
        }

        return true;
    }


}