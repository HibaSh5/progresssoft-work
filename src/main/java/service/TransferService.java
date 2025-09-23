package service;

import java.time.LocalDate;
import java.util.*;
import model.*;

public class TransferService {
    private final List<Account> accounts;
    private final List<Transfer> transfers;


    public TransferService(List<Account> accounts) {
        this.accounts = accounts;
        this.transfers = new ArrayList<>();
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public List<Transfer> getTransfers() {
        return transfers;
    }

    public void performTransfer(TransferRequest request) {
        String fromAccount = request.fromAccount();
        String beneficiaryID = request.beneficiaryID();
        String beneficiaryValue = request.beneficiaryValue();
        double amount = request.amount();

        if (amount < 1 || amount > 5000) {
            throw new IllegalArgumentException("Amount must be between 1 and 5000 JOD.");
        }

        Account debit = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(fromAccount))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (debit.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds.");
        }

        AccountType.parse(beneficiaryID).validate(beneficiaryValue);

        debit.setBalance(debit.getBalance() - amount);

        transfers.add(new Transfer(fromAccount, beneficiaryValue, amount, LocalDate.now()));
    }
}
