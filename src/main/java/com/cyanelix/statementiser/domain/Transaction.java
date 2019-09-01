package com.cyanelix.statementiser.domain;

import java.time.ZonedDateTime;

public class Transaction {
    private final String id;
    private final ZonedDateTime created;
    private final String description;
    private final int amount;
    private final Integer runningBalance;

    public Transaction(String id, ZonedDateTime created, String description, int amount) {
        this.id = id;
        this.created = created;
        this.description = description;
        this.amount = amount;
        this.runningBalance = null;
    }

    public Transaction(String id, ZonedDateTime created, String description, int amount, Integer runningBalance) {
        this.id = id;
        this.created = created;
        this.description = description;
        this.amount = amount;
        this.runningBalance = runningBalance;
    }

    public Transaction(Transaction transaction, int runningBalance) {
        this.id = transaction.id;
        this.created = transaction.created;
        this.description = transaction.description;
        this.amount = transaction.amount;
        this.runningBalance = runningBalance;
    }

    public String getId() {
        return id;
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public String getDescription() {
        return description;
    }

    public int getAmount() {
        return amount;
    }

    public Integer getRunningBalance() {
        return runningBalance;
    }
}
