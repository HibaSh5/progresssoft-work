package util;

public class Validator {

    //Jordanian IBAN - Format validation (structure, length, and allowed characters)
    //Checksum validation via the MOD-97 algorithm
    public static boolean isValidIban(String iban) {
        if (iban == null || !iban.matches("JO\\d{2}[A-Z0-9]{26}")) {
            return false;
        }
        return mod97Check(iban);
    }

    //The MOD-97 algorithm is the official method used to ensure that an IBAN is mathematically valid — it helps catch typos or incorrect digits.
    private static boolean mod97Check(String iban) {
        // Move first 4 chars to the end
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        
        // Replace letters with digits (A=10, B=11, ..., Z=35)
        StringBuilder numericIban = new StringBuilder();
        for (char ch : rearranged.toCharArray()) {
            if (Character.isLetter(ch)) {
                numericIban.append(Character.getNumericValue(ch));
            } else {
                numericIban.append(ch);
            }
        }

        // Compute mod-97
        String numStr = numericIban.toString();
        int mod = 0;
        for (int i = 0; i < numStr.length(); i += 7) {
            String part = mod + numStr.substring(i, Math.min(i + 7, numStr.length()));
            mod = Integer.parseInt(part) % 97;
        }

        return mod == 1;
    }

    //checks whether a mobile number is valid according to a specific Jordanian format
    public static boolean isValidMobile(String mobile) {
        return mobile != null && mobile.matches("009627\\d{8}");
    }

    //checks whether a beneficiary alias is valid
    //RULE: only consist of characters and digits, start with a character, and its length should not exceed 20
    public static boolean isValidAlias(String alias) {
        return alias != null && alias.matches("^[A-Za-z][A-Za-z0-9]{0,19}$");
    }

    //isValidBeneficiary should return true if the given beneficiary is either:
    // valid IBAN or valid mobile number or valid alias
    public static boolean isValidBeneficiary(String input) {
        return isValidIban(input) || isValidMobile(input) || isValidAlias(input);
    }
}