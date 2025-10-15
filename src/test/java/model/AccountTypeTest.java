package model;

import org.junit.jupiter.api.Test;

import static model.AccountType.*;
import static org.junit.jupiter.api.Assertions.*;

class AccountTypeTest {
    @Test
    void testParse() {
        assertEquals(IBAN, parse("1"));
        assertEquals(Mobile, parse("2"));
        assertEquals(Alias, parse("3"));
        assertThrows(IllegalArgumentException.class, () -> parse("4"));
    }

    @Test
    void testIBanValidation() {
        assertValidationSuccess(IBAN, "JO94CBJO0010000000000131000302");
        assertValidationFailure(IBAN, "ABC", "Invalid IBAN for ABC");
    }

    @Test
    void testMobileValidation() {
        assertValidationSuccess(Mobile, "00962791234567");
        assertValidationFailure(Mobile, null, "Invalid Mobile for null");
        assertValidationFailure(Mobile, "A", "Invalid Mobile for A");
        assertValidationFailure(Mobile, "123456789012345", "Invalid Mobile for 123456789012345");
        assertValidationFailure(Mobile, "12345678901234", "Invalid Mobile for 12345678901234");
        assertValidationFailure(Mobile, "1234567890123", "Invalid Mobile for 1234567890123");
        assertValidationFailure(Mobile, "0096279123456A","Invalid Mobile for 0096279123456A");
    }

    @Test
    void testAliasValidation() {
        assertValidationSuccess(Alias, "Hiba15");
        assertValidationFailure(Alias, null, "Invalid Alias for null");
        assertValidationFailure(Alias, "123", "Invalid Alias for 123");
    }

    private static void assertValidationSuccess(AccountType accountType, String value) {
        try {
            accountType.validate(value);
        }catch (Exception e) {
            fail();
        }
    }

    private static void assertValidationFailure(AccountType accountType, String value, String errorMessage) {
        var e = assertThrows(IllegalArgumentException.class, () -> accountType.validate(value));
        assertEquals(errorMessage, e.getMessage());
    }
}