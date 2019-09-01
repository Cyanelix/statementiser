package com.cyanelix.statementiser.service;

import com.cyanelix.statementiser.client.MonzoClient;
import com.cyanelix.statementiser.domain.Transaction;
import com.cyanelix.statementiser.monzo.MonzoTransaction;
import com.cyanelix.statementiser.monzo.MonzoTransactions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionsService {
    private final MonzoClient monzoClient;

    @Autowired
    public TransactionsService(MonzoClient monzoClient) {
        this.monzoClient = monzoClient;
    }

    public List<Transaction> getNewTransactions(String accountId) {
        MonzoTransactions transactions = monzoClient.getTransactions(accountId);
        if (transactions == null) {
            return Collections.emptyList();
        }

        List<MonzoTransaction> transactionList = transactions.getTransactions();
        if (transactionList == null) {
            return Collections.emptyList();
        }

        return transactionList.stream()
                .filter(monzoTransaction -> !monzoTransaction.hasBeenExported())
                .filter(transaction -> !transaction.isPotTransaction())
                .filter(transaction -> transaction.getAmount() != 0)
                .map(MonzoTransaction::toTransaction)
                .collect(Collectors.toList());
    }

    public void annotateTransactionsAsExported(List<Transaction> transactions) {
        ZonedDateTime exportTimestamp = ZonedDateTime.now();
        Map<String, String> metadata = new HashMap<>();
        metadata.put("csv_exported", exportTimestamp.format(DateTimeFormatter.ISO_DATE_TIME));

        transactions.forEach(transaction -> monzoClient.setMetadataOnTransaction(transaction.getId(), metadata));
    }
}
