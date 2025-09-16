package com.cliqtransferapi.service;

import com.cliqtransferapi.model.Account;
import com.cliqtransferapi.model.Transfer;
import com.cliqtransferapi.model.TransferRequest;
import com.cliqtransferapi.repository.AccountRepository;
import com.cliqtransferapi.repository.TransferRepository;
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
    public void performTransfer(TransferRequest request) {
        String fromAccount = request.fromAccount();
        String beneficiaryID = request.beneficiaryID();
        String beneficiaryValue = request.beneficiaryValue();
        double amount = request.amount();

        if (amount < 1 || amount > 5000) {
            throw new IllegalArgumentException("Amount must be between 1 and 5000 JOD.");
        }

        Account debit = accountRepository.findById(fromAccount)
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (debit.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds.");
        }

        if (!AccountType.parse(beneficiaryID).validate(beneficiaryValue)) {
            throw new IllegalArgumentException("Invalid beneficiary account value for type: " + AccountType.parse(beneficiaryID));
        }

        debit.setBalance(debit.getBalance() - amount);
        accountRepository.save(debit);

        // Save transfer record
        Transfer transfer = new Transfer(fromAccount, beneficiaryID, beneficiaryValue, amount, LocalDate.now());
        transferRepository.save(transfer);

    }
}
