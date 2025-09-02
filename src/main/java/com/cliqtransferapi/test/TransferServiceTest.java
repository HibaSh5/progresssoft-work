package com.cliqtransferapi.test;

import com.cliqtransferapi.model.Account;
import com.cliqtransferapi.model.Transfer;
import com.cliqtransferapi.repository.AccountRepository;
import com.cliqtransferapi.repository.TransferRepository;
import com.cliqtransferapi.service.TransferService;
import com.cliqtransferapi.util.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional //no need to do any updates / addition data to the postgres tables
class TransferServiceTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private AccountRepository accountRepository;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private TransferRepository transferRepository;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(accountRepository, transferRepository);
    }

    @Test
    void performTransfer_Success_IBAN() {
        String fromAccount = "1223123";
        String beneficiaryID = "IBAN";
        String beneficiaryValue = "JO94CBJO0010000000000131000302";
        double amount = 500;

        try (MockedStatic<Validator> mockedValidator = org.mockito.Mockito.mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidIban(beneficiaryValue)).thenReturn(true);

            transferService.performTransfer(fromAccount, beneficiaryID, beneficiaryValue, amount);

            Account updatedAccount = accountRepository.findById(fromAccount).orElseThrow();

            Transfer t = transferRepository.findAll().get(transferRepository.findAll().size() - 1);

        }
    }

    @Test
    void performTransfer_Success_Mobile() {
        String fromAccount = "1202091";
        String beneficiaryID = "Mobile";
        String beneficiaryValue = "00962791234567";
        double amount = 200;

        try (MockedStatic<Validator> mockedValidator = org.mockito.Mockito.mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidMobile(beneficiaryValue)).thenReturn(true);

            Account accountBefore = accountRepository.findById(fromAccount).orElseThrow();
            double initialBalance = accountBefore.getBalance();

            transferService.performTransfer(fromAccount, beneficiaryID, beneficiaryValue, amount);

            Account accountAfter = accountRepository.findById(fromAccount).orElseThrow();
            assertEquals(initialBalance - amount, accountAfter.getBalance(), 0.01);

            Transfer t = transferRepository.findAll().get(transferRepository.findAll().size() - 1);
            assertEquals("Mobile", t.getBeneficiary());
            assertEquals(amount, t.getAmount());
        }
    }

    @Test
    void performTransfer_Success_Alias() {
        String fromAccount = "7272819";
        String beneficiaryID = "Alias";
        String beneficiaryValue = "hiba15";
        double amount = 100;

        try (MockedStatic<Validator> mockedValidator = org.mockito.Mockito.mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidAlias(beneficiaryValue)).thenReturn(true);

            Account accountBefore = accountRepository.findById(fromAccount).orElseThrow();
            double initialBalance = accountBefore.getBalance();

            transferService.performTransfer(fromAccount, beneficiaryID, beneficiaryValue, amount);

            Account accountAfter = accountRepository.findById(fromAccount).orElseThrow();
            assertEquals(initialBalance - amount, accountAfter.getBalance(), 0.01);

            Transfer t = transferRepository.findAll().get(transferRepository.findAll().size() - 1);
            assertEquals("Alias", t.getBeneficiary());
            assertEquals(amount, t.getAmount());
        }
    }

    @Test
    void performTransfer_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> {
            transferService.performTransfer("1202091", "IBAN", "JO94CBJO0010000000000131000302", 0);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            transferService.performTransfer("1223123", "IBAN", "JO94CBJO0010000000000131000302", 6000);
        });
    }

    @Test
    void performTransfer_AccountNotFound() {
        assertThrows(IllegalArgumentException.class, () -> {
            transferService.performTransfer("nonexistent", "IBAN", "JO94CBJO0010000000000131000302", 100);
        });
    }

    @Test
    void performTransfer_InvalidBeneficiary_IBAN() {
        try (MockedStatic<Validator> mockedValidator = org.mockito.Mockito.mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidIban("BAD_IBAN")).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () -> {
                transferService.performTransfer("7272819", "IBAN", "BAD_IBAN", 100);
            });
        }
    }

    @Test
    void performTransfer_InvalidBeneficiary_Mobile() {
        try (MockedStatic<Validator> mockedValidator = org.mockito.Mockito.mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidMobile("1234")).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () -> {
                transferService.performTransfer("1223123", "Mobile", "1234", 100);
            });
        }
    }

    @Test
    void performTransfer_InvalidBeneficiary_Alias() {
        try (MockedStatic<Validator> mockedValidator = org.mockito.Mockito.mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidAlias("!!!")).thenReturn(false);

            assertThrows(IllegalArgumentException.class, () -> {
                transferService.performTransfer("1202091", "Alias", "!!!", 100);
            });
        }
    }

    @Test
    void performTransfer_InvalidBeneficiaryType() {
        assertThrows(IllegalArgumentException.class, () -> {
            transferService.performTransfer("1223123", "9", "something", 100);
        });
    }
}
