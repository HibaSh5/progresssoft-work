package com.cliqtransferapi.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false)
    private Long id;

    @Column(name = "from_account", nullable = false, length = 50)
    private String debitAccount;

    @Column(name = "beneficiary_type", nullable = false, length = 100)
    private String beneficiary;

    @Column(name = "beneficiary_value", nullable = false)
    private String beneficiary_value;

    @Column(name = "amount", nullable = false)
    private double amount;

    @Column(name = "transfer_date", nullable = false)
    private LocalDate date;

    public Transfer() {
        // JPA requires a default constructor
    }

    public Transfer(String debitAccount, String beneficiary, String beneficiaryValue, double amount, LocalDate date) {
        this.debitAccount = debitAccount;
        this.beneficiary = beneficiary;
        this.beneficiary_value = beneficiaryValue;
        this.amount = amount;
        this.date = date;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public String getDebitAccount() {
        return debitAccount;
    }

    public void setDebitAccount(String debitAccount) {
        this.debitAccount = debitAccount;
    }

    public String getBeneficiary() {
        return beneficiary;
    }

    public void setBeneficiary(String beneficiary) {
        this.beneficiary = beneficiary;
    }

    public String getBeneficiaryValue() {
        return beneficiary_value;
    }

    public void setBeneficiaryValue(String vbeneficiary) {
        this.beneficiary_value = vbeneficiary;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "From: " + debitAccount + " -> To: " + beneficiary + " | Amount: " + amount + " JOD | Date: " + date;
    }
}
