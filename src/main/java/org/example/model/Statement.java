package org.example.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Statement {
    private String transactionReference; // :20:
    private String account; // :25:
    private String statementNumber; // :28C:
    private Balance openingBalance; // from :60F: or :60M:
    private Balance closingBalance; // from :62F: or :62M:
    private final List<Transaction> transactions = new ArrayList<>();

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getStatementNumber() {
        return statementNumber;
    }

    public void setStatementNumber(String statementNumber) {
        this.statementNumber = statementNumber;
    }

    public Balance getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Balance openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Balance getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Balance closingBalance) {
        this.closingBalance = closingBalance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getTotalCredits() {
        return transactions.stream()
                .filter(Transaction::isCredit)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalDebits() {
        return transactions.stream()
                .filter(t -> !t.isCredit())
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
