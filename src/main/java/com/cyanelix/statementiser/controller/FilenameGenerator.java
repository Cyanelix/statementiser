package com.cyanelix.statementiser.controller;

import com.cyanelix.statementiser.domain.Transaction;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

@Component
public class FilenameGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public String generateCsvFilename(String accountDescription, List<Transaction> transactions) {
        Objects.requireNonNull(accountDescription);
        Objects.requireNonNull(transactions);

        if (accountDescription.isEmpty()) {
            throw new IllegalArgumentException("Account description is required");
        }

        String dateRange;
        if (transactions.isEmpty()) {
            dateRange = "empty";
        } else {
            ZonedDateTime firstTransactionDate = transactions.get(0).getCreated();
            ZonedDateTime lastTransactionDate = transactions.get(transactions.size() - 1).getCreated();

            dateRange = String.format("%s..%s",
                    firstTransactionDate.format(DATE_FORMATTER),
                    lastTransactionDate.format(DATE_FORMATTER));
        }

        return String.format("%s_%s.csv", accountDescription, dateRange);
    }
}
