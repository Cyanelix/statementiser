package com.cyanelix.statementiser.domain;

import java.time.ZonedDateTime;

public class MonzoTransaction {
    private ZonedDateTime created;
    private String description;
    private int amount;
    private int calculatedBalance;

    public MonzoTransaction() { }

    public MonzoTransaction(ZonedDateTime created, String description, int amount) {
        this.created = created;
        this.description = description;
        this.amount = amount;
    }

    public MonzoTransaction(ZonedDateTime created, String description, int amount, int calculatedBalance) {
        this(created, description, amount);
        this.calculatedBalance = calculatedBalance;
    }

    public MonzoTransaction(MonzoTransaction sourceTransaction, int calculatedBalance) {
        this(sourceTransaction.created, sourceTransaction.description, sourceTransaction.amount);
        this.calculatedBalance = calculatedBalance;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public void setCreated(ZonedDateTime created) {
        this.created = created;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCalculatedBalance() {
        return calculatedBalance;
    }

    public void setCalculatedBalance(int calculatedBalance) {
        this.calculatedBalance = calculatedBalance;
    }
}
