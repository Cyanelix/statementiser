package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.Transaction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class BalanceCalculator {
    public List<Transaction> calculateBalances(int currentBalance, List<Transaction> transactions) {
        Objects.requireNonNull(transactions);

        List<Transaction> transactionsWithBalances = new ArrayList<>(transactions.size());
        int previousAmount = 0;
        int runningBalance = currentBalance;
        for (int i = transactions.size() - 1; i > -1; i--) {
            Transaction transaction = transactions.get(i);

            runningBalance = runningBalance - previousAmount;
            transactionsWithBalances.add(new Transaction(transaction, runningBalance));
            previousAmount = transaction.getAmount();

        }
        Collections.reverse(transactionsWithBalances);

        return transactionsWithBalances;
    }
}
