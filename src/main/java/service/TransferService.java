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

    public void performTransfer(String fromAccount, String beneficiaryID, String beneficiaryValue, double amount) {
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

        String beneficiary = switch (beneficiaryID) {
            case "1" -> {
                if (!Validator.isValidIban(beneficiaryValue)) {
                    throw new IllegalArgumentException("Invalid IBAN format.");
                }
                yield "IBAN";
            }
            case "2" -> {
                if (!Validator.isValidMobile(beneficiaryValue)) {
                    throw new IllegalArgumentException("Invalid mobile format.");
                }
                yield "Mobile";
            }
            case "3" -> {
                if (!Validator.isValidAlias(beneficiaryValue)) {
                    throw new IllegalArgumentException("Invalid alias format.");
                }
                yield "Alias";
            }
            default -> throw new IllegalArgumentException("Invalid beneficiary type.");
        };

        debit.setBalance(debit.getBalance() - amount);
        transfers.add(new Transfer(fromAccount, beneficiary, amount, LocalDate.now()));
    }
}
