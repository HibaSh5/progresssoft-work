package test;

import data.AccountCSVReader;
import modal.*;
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

        TransferRequest trequest = new TransferRequest(fromAccount, beneficiaryID, beneficiaryValue, amount);
        transferService.performTransfer(trequest);

        Account account = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(fromAccount))
                .findFirst()
                .orElseThrow();


        Transfer t = transferService.getTransfers().get(0);
        assertEquals("JO94CBJO0010000000000131000302", t.getBeneficiary()); //IBAN
        assertEquals(amount, t.getAmount());
        assertEquals(LocalDate.now(), t.getDate());
    }

    @Test
    void performTransfer_Success_Mobile() {
        String fromAccount = "1202091";
        String beneficiaryID = "2";
        String beneficiaryValue = "00962791234567";
        double amount = 200;

        TransferRequest trequest = new TransferRequest(fromAccount, beneficiaryID, beneficiaryValue, amount);
        transferService.performTransfer(trequest);

        Account account = accounts.stream()
                .filter(a -> a.getAccountNumber().equals(fromAccount))
                .findFirst()
                .orElseThrow();

        assertEquals(300, account.getBalance(), 0.001); // Assuming initial was 500

        Transfer t = transferService.getTransfers().get(transferService.getTransfers().size() - 1);
        assertEquals("00962791234567", t.getBeneficiary());
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

        TransferRequest trequest = new TransferRequest(fromAccount, beneficiaryID, beneficiaryValue, amount);
        transferService.performTransfer(trequest);

        assertEquals(initialBalance - amount, account.getBalance(), 0.001);

        Transfer t = transferService.getTransfers().get(transferService.getTransfers().size() - 1);
        assertEquals("hiba15", t.getBeneficiary());
        assertEquals(amount, t.getAmount());
    }
}

