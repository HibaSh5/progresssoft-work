package com.cliqtransferapi.service;

import com.cliqtransferapi.model.Account;
import com.cliqtransferapi.model.Transfer;
import com.cliqtransferapi.repository.AccountRepository;
import com.cliqtransferapi.repository.TransferRepository;
import com.cliqtransferapi.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public List<Transfer> getTransfers() {
        return transferRepository.findAll();
    }

    @Transactional
    public boolean performTransfer(String fromAccount, String beneficiaryID, String beneficiaryValue, double amount) {
        if (amount < 1 || amount > 5000) {
            throw new IllegalArgumentException("Amount must be between 1 and 5000 JOD.");
        }

        Account debit = accountRepository.findById(fromAccount)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (debit.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds.");
        }

        String beneficiary = switch (beneficiaryID) {
            case "IBAN" -> {
                if (!Validator.isValidIban(beneficiaryValue)) {
                    throw new IllegalArgumentException("Invalid IBAN format.");
                }
                yield "IBAN";
            }
            case "Mobile" -> {
                if (!Validator.isValidMobile(beneficiaryValue)) {
                    throw new IllegalArgumentException("Invalid mobile format.");
                }
                yield "Mobile";
            }
            case "Alias" -> {
                if (!Validator.isValidAlias(beneficiaryValue)) {
                    throw new IllegalArgumentException("Invalid alias format.");
                }
                yield "Alias";
            }
            default -> throw new IllegalArgumentException("Invalid beneficiary type.");
        };

        debit.setBalance(debit.getBalance() - amount);
        accountRepository.save(debit);

        // Save transfer record
        Transfer transfer = new Transfer(fromAccount, beneficiary, beneficiaryValue, amount, LocalDate.now());
        transferRepository.save(transfer);

        return true;
    }
}
