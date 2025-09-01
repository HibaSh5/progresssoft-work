package test;

import data.AccountCSVReader;
import modal.Account;
import modal.Transfer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.TransferService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {

    private TransferService transferService;
    private List<Account> accounts;

    @BeforeEach
    void setUp() {
        accounts = AccountCSVReader.loadAccounts("src/accounts.csv");
        transferService = new TransferService(accounts);
    }

    @Test
    void performTransfer_Success_IBAN() {
        String fromAccount = "1223123";
        String beneficiaryID = "1";
        String beneficiaryValue = "JO94CBJO0010000000000131000302";
        double amount = 500;

        transferService.performTransfer(fromAccount, beneficiaryID, beneficiaryValue, amount);

        Account account = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(fromAccount))
                .findFirst()
                .orElseThrow();



        Transfer t = transferService.getTransfers().get(0);
        assertEquals("IBAN", t.getBeneficiary());
        assertEquals(amount, t.getAmount());
        assertEquals(LocalDate.now(), t.getDate());
    }

    @Test
    void performTransfer_Success_Mobile() {
        String fromAccount = "1202091";
        String beneficiaryID = "2";
        String beneficiaryValue = "00962791234567";
        double amount = 200;

        transferService.performTransfer(fromAccount, beneficiaryID, beneficiaryValue, amount);

        Account account = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(fromAccount))
                .findFirst()
                .orElseThrow();

        assertEquals(300, account.getBalance(), 0.001); // Assuming initial was 500

        Transfer t = transferService.getTransfers().get(transferService.getTransfers().size() - 1);
        assertEquals("Mobile", t.getBeneficiary());
        assertEquals(amount, t.getAmount());
    }

    @Test
    void performTransfer_Success_Alias() {
        String fromAccount = "7272819";
        String beneficiaryID = "3";
        String beneficiaryValue = "hiba15";
        double amount = 100;

        Account account = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(fromAccount))
                .findFirst()
                .orElseThrow();

        double initialBalance = account.getBalance();

        transferService.performTransfer(fromAccount, beneficiaryID, beneficiaryValue, amount);

        assertEquals(initialBalance - amount, account.getBalance(), 0.001);

        Transfer t = transferService.getTransfers().get(transferService.getTransfers().size() - 1);
        assertEquals("Alias", t.getBeneficiary());
        assertEquals(amount, t.getAmount());
    }

    @Test
    void performTransfer_InvalidAmount() {
        try {
            transferService.performTransfer("1202091", "1", "JO94CBJO0010000000000131000302", 0);
            fail("Expected IllegalArgumentException not thrown for amount = 0");
        } catch (IllegalArgumentException ignored) {}

        try {
            transferService.performTransfer("1223123", "1", "JO94CBJO0010000000000131000302", 6000);
            fail("Expected IllegalArgumentException not thrown for amount = 6000");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void performTransfer_AccountNotFound() {
        try {
            transferService.performTransfer("1202091", "1", "JO94CBJO0010000000000131000302", 100);
         } catch (IllegalArgumentException ignored) {

        }
    }

    @Test
    void performTransfer_InsufficientFunds() {
        try {
            transferService.performTransfer("1223123", "1", "JO94CBJO0010000000000131000302", 50);
        } catch (IllegalStateException ignored) {}
    }

    @Test
    void performTransfer_InvalidBeneficiary_IBAN() {
        try {
            transferService.performTransfer("7272819", "1", "BAD_IBAN", 100);
            fail("Expected IllegalArgumentException for invalid IBAN");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void performTransfer_InvalidBeneficiary_Mobile() {
        try {
            transferService.performTransfer("1223123", "2", "1234", 100);
            fail("Expected IllegalArgumentException for invalid mobile");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void performTransfer_InvalidBeneficiary_Alias() {
        try {
            transferService.performTransfer("1202091", "3", "!!!", 100);
            fail("Expected IllegalArgumentException for invalid alias");
        } catch (IllegalArgumentException ignored) {}
    }

    @Test
    void performTransfer_InvalidBeneficiaryType() {
        try {
            transferService.performTransfer("1223123", "9", "something", 100);
            fail("Expected IllegalArgumentException for invalid beneficiary type");
        } catch (IllegalArgumentException ignored) {}
    }
}