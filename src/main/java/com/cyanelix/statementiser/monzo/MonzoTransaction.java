package com.cyanelix.statementiser.monzo;

import com.cyanelix.statementiser.domain.Transaction;

import java.time.ZonedDateTime;

public class MonzoTransaction {
    private String id;
    private ZonedDateTime created;
    private ZonedDateTime settled;
    private String description;
    private int amount;
    private MonzoMetadata metadata;

    public MonzoTransaction() { }

    public MonzoTransaction(ZonedDateTime created, String description, int amount) {
        this.created = created;
        this.description = description;
        this.amount = amount;
    }

    public Transaction toTransaction() {
        return new Transaction(id, created, description, amount);
    }

    public boolean hasBeenExported() {
        return metadata != null && metadata.getCsvExported() != null;
    }

    public boolean isPotTransaction() {
        return metadata != null && metadata.getPotAccountId() != null && !metadata.getPotAccountId().isEmpty();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public MonzoMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(MonzoMetadata metadata) {
        this.metadata = metadata;
    }

    public ZonedDateTime getSettled() {
        return settled;
    }

    public void setSettled(ZonedDateTime settled) {
        this.settled = settled;
    }
}
