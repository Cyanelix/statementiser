package com.cyanelix.statementiser.controller;

import com.cyanelix.statementiser.domain.MonzoTransaction;
import com.cyanelix.statementiser.domain.MonzoTransactions;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
public class FilenameGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String generateCsvFilename(String accountDescription, MonzoTransactions transactions) {
        Objects.requireNonNull(accountDescription);
        Objects.requireNonNull(transactions);

        if (accountDescription.isEmpty()) {
            throw new IllegalArgumentException("Account description is required");
        }

        List<MonzoTransaction> transactionList = transactions.getTransactions();
        ZonedDateTime firstTransactionDate = transactionList.get(0).getCreated();
        ZonedDateTime lastTransactionDate = transactionList.get(transactionList.size() - 1).getCreated();

        return String.format("%s_%s..%s.csv",
                accountDescription,
                firstTransactionDate.format(DATE_FORMATTER),
                lastTransactionDate.format(DATE_FORMATTER));
    }
}
