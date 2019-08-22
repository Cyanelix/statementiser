package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.MonzoTransaction;
import com.cyanelix.statementiser.domain.MonzoTransactions;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MonzoTransactionsToCsvConverter implements Converter<MonzoTransactions, String> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public String convert(MonzoTransactions monzoTransactions) {
        return monzoTransactions.getTransactions().stream()
                .map(this::transactionToCsvLine)
                .collect(Collectors.joining("\n"));
    }

    private String transactionToCsvLine(MonzoTransaction transaction) {
        Objects.requireNonNull(transaction.getCreated());
        Objects.requireNonNull(transaction.getDescription());

        List<String> values = Arrays.asList(
                transaction.getCreated().format(DATE_TIME_FORMATTER),
                transaction.getDescription(),
                convertAmountFromPence(transaction.getAmount()),
                convertAmountFromPence(transaction.getCalculatedBalance()));

        return values.stream().collect(Collectors.joining("\",\"", "\"", "\""));
    }

    private String convertAmountFromPence(int amount) {
        return new BigDecimal(amount).movePointLeft(2).toPlainString();
    }
}
