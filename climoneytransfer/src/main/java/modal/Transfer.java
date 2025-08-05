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

    // Getters and toString()
    @Override
    public String toString() {
        return "From: " + debitAccount + " -> To: " + beneficiary + " | Amount: " + amount + " JOD | Date: "
                + date;
    }
}