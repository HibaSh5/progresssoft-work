package service;

import java.time.LocalDate;
import java.util.*;
import modal.*;
import util.*;

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

    public boolean performTransfer(String fromAccount, String beneficiaryID, String beneficiaryValue, double amount) {
        if (amount < 1 || amount > 5000) {
            throw new IllegalArgumentException("Amount must be between 1 and 5000 JOD.");
        }

        String beneficiary = "";
        switch (beneficiaryID) {
            case "1":
                beneficiary = "IBAN";
                break;
            case "2":
                beneficiary = "Mobile";
                break;
            case "3":
                beneficiary = "Alias";
                break;
            default:
                throw new IllegalArgumentException("Invalid beneficiary format.");
        }

        Account debit = accounts.stream()
                .filter(acc -> acc.getAccountNumber().equals(fromAccount))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account not found."));

        if (debit.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds.");
        }

        if (!Validator.isValidBeneficiary(beneficiaryValue)) {
            throw new IllegalArgumentException("Invalid beneficiary format.");
        }

        debit.setBalance(debit.getBalance() - amount);
        transfers.add(new Transfer(fromAccount, beneficiary, amount, LocalDate.now()));
        return true;
    }
}
