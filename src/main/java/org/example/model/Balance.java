package org.example.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Balance {
    private boolean credit; // true = credit (C), false = debit (D)
    private LocalDate date;
    private String currency;
    private BigDecimal amount;

    public Balance() {}

    public Balance(boolean credit, LocalDate date, String currency, BigDecimal amount) {
        this.credit = credit;
        this.date = date;
        this.currency = currency;
        this.amount = amount;
    }

    public boolean isCredit() {
        return credit;
    }

    public void setCredit(boolean credit) {
        this.credit = credit;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getFormattedAmount() {
        return String.format("%s %.2f", currency, amount);
    }

    public String getType() {
        return credit ? "Credit" : "Debit";
    }
}
