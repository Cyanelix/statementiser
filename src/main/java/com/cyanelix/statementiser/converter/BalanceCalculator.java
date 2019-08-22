package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.MonzoTransaction;
import com.cyanelix.statementiser.domain.MonzoTransactions;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class BalanceCalculator {
    public MonzoTransactions calculateBalances(int currentBalance, MonzoTransactions monzoTransactions) {
        Objects.requireNonNull(monzoTransactions);

        List<MonzoTransaction> transactions = monzoTransactions.getTransactions();
        Collections.reverse(transactions);

        List<MonzoTransaction> transactionsWithBalances = new ArrayList<>(transactions.size());
        int previousAmount = 0;
        int runningBalance = currentBalance;
        for (MonzoTransaction transaction : transactions) {
            runningBalance = runningBalance - previousAmount;
            transactionsWithBalances.add(new MonzoTransaction(transaction, runningBalance));
            previousAmount = transaction.getAmount();

        }
        Collections.reverse(transactionsWithBalances);

        return new MonzoTransactions(transactionsWithBalances);
    }
}
