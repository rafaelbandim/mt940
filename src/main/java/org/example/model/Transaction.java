package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private LocalDate date;
    private boolean credit; // true = credit, false = debit
    private BigDecimal amount;
    private String description;

    public Transaction() {}

    public Transaction(LocalDate date, boolean credit, BigDecimal amount, String description) {
        this.date = date;
        this.credit = credit;
        this.amount = amount;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isCredit() {
        return credit;
    }

    public void setCredit(boolean credit) {
        this.credit = credit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
