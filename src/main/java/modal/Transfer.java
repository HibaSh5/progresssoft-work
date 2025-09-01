package modal;

import java.time.LocalDate;

public class Transfer {
    private String debitAccount;
    private String beneficiary;
    private double amount;
    private LocalDate date;

    public Transfer(String debitAccount, String beneficiary, double amount, LocalDate date) {
        this.debitAccount = debitAccount;
        this.beneficiary = beneficiary;
        this.amount = amount;
        this.date = date;
    }

    // Getters
    public String getDebitAccount() {
        return debitAccount;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    // Setters
    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "From: " + debitAccount + " -> To: " + beneficiary + " | Amount: " + amount + " JOD | Date: " + date;
    }
}
