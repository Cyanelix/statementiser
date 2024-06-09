package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.Transaction;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TransactionsToCsvConverter implements Converter<List<Transaction>, String> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String convert(List<Transaction> monzoTransactions) {
        return monzoTransactions.stream()
                .map(this::transactionToCsvLine)
                .collect(Collectors.joining("\n"));
    }

    private String transactionToCsvLine(Transaction transaction) {
        Objects.requireNonNull(transaction.getCreated());
        Objects.requireNonNull(transaction.getDescription());

        List<String> values = Arrays.asList(
                transaction.getCreated().format(DATE_TIME_FORMATTER),
                transaction.getDescription(),
                convertAmountFromPence(transaction.getAmount()),
                convertAmountFromPence(transaction.getRunningBalance()));

        return values.stream().collect(Collectors.joining("\",\"", "\"", "\""));
    }

    private String convertAmountFromPence(int amount) {
        return new BigDecimal(amount).movePointLeft(2).toPlainString();
    }
}
