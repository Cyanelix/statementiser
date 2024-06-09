package com.cyanelix.statementiser.monzo;

import java.util.List;

public class MonzoTransactions {
    private List<MonzoTransaction> transactions;

    public MonzoTransactions() { }

    public MonzoTransactions(List<MonzoTransaction> transactions) {
        this.transactions = transactions;
    }

    public List<MonzoTransaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<MonzoTransaction> transactions) {
        this.transactions = transactions;
    }
}
