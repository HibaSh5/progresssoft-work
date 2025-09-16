package com.cliqtransferapi.test;

import com.cliqtransferapi.controller.GlobalHandlerException;
import com.cliqtransferapi.model.Account;
import com.cliqtransferapi.model.Transfer;
import com.cliqtransferapi.model.TransferRequest;
import com.cliqtransferapi.repository.AccountRepository;
import com.cliqtransferapi.repository.TransferRepository;
import com.cliqtransferapi.service.TransferService;
import com.cliqtransferapi.util.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TransferServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    private GlobalHandlerException handler;

    private List<Transfer> transfers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new GlobalHandlerException();
        transfers = new ArrayList<>();
        transferService = new TransferService(accountRepository, transferRepository);
    }

    @Test
    void performTransfer_Success_IBAN() {
        String fromAccount = "1223123";
        String beneficiaryID = "IBAN";
        String beneficiaryValue = "JO94CBJO0010000000000131000302";
        double amount = 500;

        Account accountBefore = new Account();
        accountBefore.setAccountNumber(fromAccount);
        accountBefore.setBalance(1000);

        when(accountRepository.findById(fromAccount)).thenReturn(Optional.of(accountBefore));

        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            accountBefore.setBalance(acc.getBalance());
            return acc;
        });

        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer t = invocation.getArgument(0);
            transfers.add(t);
            return t;
        });

        when(transferRepository.findAll()).thenReturn(transfers);

        try (MockedStatic<Validator> mockedValidator = mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidIban(beneficiaryValue)).thenReturn(true);

            TransferRequest trequest = new TransferRequest(fromAccount, beneficiaryID, beneficiaryValue, amount);

            transferService.performTransfer(trequest);

            assertEquals(500, accountBefore.getBalance(), 0.01);

            assertFalse(transfers.isEmpty(), "Transfer list should not be empty");
            Transfer lastTransfer = transfers.get(transfers.size() - 1);
            assertEquals("IBAN", lastTransfer.getBeneficiary());
            assertEquals(amount, lastTransfer.getAmount(), 0.01);
        }
    }

    @Test
    void performTransfer_Success_Mobile() {
        String fromAccount = "1202091";
        String beneficiaryID = "Mobile";
        String beneficiaryValue = "00962791234567";
        double amount = 200;

        Account accountBefore = new Account();
        accountBefore.setAccountNumber(fromAccount);
        accountBefore.setBalance(500);

        when(accountRepository.findById(fromAccount)).thenReturn(Optional.of(accountBefore));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            accountBefore.setBalance(acc.getBalance());
            return acc;
        });
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer t = invocation.getArgument(0);
            transfers.add(t);
            return t;
        });
        when(transferRepository.findAll()).thenReturn(transfers);

        try (MockedStatic<Validator> mockedValidator = mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidMobile(beneficiaryValue)).thenReturn(true);

            TransferRequest trequest = new TransferRequest(fromAccount, beneficiaryID, beneficiaryValue, amount);

            transferService.performTransfer(trequest);

            assertEquals(300, accountBefore.getBalance(), 0.01);

            assertFalse(transfers.isEmpty());
            Transfer lastTransfer = transfers.get(transfers.size() - 1);
            assertEquals("Mobile", lastTransfer.getBeneficiary());
            assertEquals(amount, lastTransfer.getAmount(), 0.01);
        }
    }

    @Test
    void performTransfer_Success_Alias() {
        String fromAccount = "7272819";
        String beneficiaryID = "Alias";
        String beneficiaryValue = "hiba15";
        double amount = 100;

        Account accountBefore = new Account();
        accountBefore.setAccountNumber(fromAccount);
        accountBefore.setBalance(300);

        when(accountRepository.findById(fromAccount)).thenReturn(Optional.of(accountBefore));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            accountBefore.setBalance(acc.getBalance());
            return acc;
        });
        when(transferRepository.save(any(Transfer.class))).thenAnswer(invocation -> {
            Transfer t = invocation.getArgument(0);
            transfers.add(t);
            return t;
        });
        when(transferRepository.findAll()).thenReturn(transfers);

        try (MockedStatic<Validator> mockedValidator = mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidAlias(beneficiaryValue)).thenReturn(true);

            TransferRequest trequest = new TransferRequest(fromAccount, beneficiaryID, beneficiaryValue, amount);

            transferService.performTransfer(trequest);

            assertEquals(200, accountBefore.getBalance(), 0.01);

            assertFalse(transfers.isEmpty());
            Transfer lastTransfer = transfers.get(transfers.size() - 1);
            assertEquals("Alias", lastTransfer.getBeneficiary());
            assertEquals(amount, lastTransfer.getAmount(), 0.01);
        }
    }

    @Test
    void performTransfer_InvalidIBAN_ShouldThrowException() {
        String fromAccount = "1223123";
        String beneficiaryID = "IBAN";
        String beneficiaryValue = "INVALID_IBAN";
        double amount = 500;

        Account accountBefore = new Account();
        accountBefore.setAccountNumber(fromAccount);
        accountBefore.setBalance(1000);

        when(accountRepository.findById(fromAccount)).thenReturn(Optional.of(accountBefore));

        try (MockedStatic<Validator> mockedValidator = mockStatic(Validator.class)) {
            mockedValidator.when(() -> Validator.isValidIban(beneficiaryValue)).thenReturn(false);

            TransferRequest trequest = new TransferRequest(fromAccount, beneficiaryID, beneficiaryValue, amount);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                transferService.performTransfer(trequest);
            });

            String result = handler.handleException(exception);
            // Replace the below with your actual expected error message from GlobalHandlerException
            assertEquals("Invalid IBAN format.", result);
        }
    }

}
