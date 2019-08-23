package com.cyanelix.statementiser.domain;

import java.time.ZonedDateTime;
import java.util.Map;

public class MonzoTransaction {
    private static final String CSV_EXPORTED_KEY = "csv-exported";

    private String id;
    private ZonedDateTime created;
    private String description;
    private int amount;
    private int calculatedBalance;
    private Map<String, String> metadata;

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

    public boolean hasNotBeenExported() {
        return metadata == null || !metadata.containsKey(CSV_EXPORTED_KEY);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}
