package com.cyanelix.statementiser.service;

import com.cyanelix.statementiser.client.MonzoClient;
import com.cyanelix.statementiser.domain.Transaction;
import com.cyanelix.statementiser.monzo.MonzoMetadata;
import com.cyanelix.statementiser.monzo.MonzoTransaction;
import com.cyanelix.statementiser.monzo.MonzoTransactions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TransactionsServiceTest {
    @Mock
    private MonzoClient monzoClient;

    @InjectMocks
    private TransactionsService transactionsService;

    @Test
    public void nullTransactions_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        given(monzoClient.getTransactions(accountId)).willReturn(null);

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void nullTransactionsList_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(null));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void emptyTransactionsList_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.emptyList()));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void singleTransactionWithZeroValue_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(createTransaction(0))));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void singleTransaction_getNewTransactions_returnsSingleTransaction() {
        // Given...
        String accountId = "foo";
        MonzoTransaction monzoTransaction = createTransaction(1);
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(monzoTransaction)));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).hasSize(1);
        assertTransactionEqualsMonzoTransaction(transactions.get(0), monzoTransaction);
    }

    @Test
    public void singleTransactionWithNullMetadata_getNewTransactions_returnsTransaction() {
        // Given...
        String accountId = "foo";
        MonzoTransaction monzoTransaction = createTransaction(1);
        monzoTransaction.setMetadata(null);

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(monzoTransaction)));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).hasSize(1);
        assertTransactionEqualsMonzoTransaction(transactions.get(0), monzoTransaction);
    }

    @Test
    public void singleTransactionWithNullCsvExported_getNewTransactions_returnsTransaction() {
        // Given...
        String accountId = "foo";
        MonzoTransaction monzoTransaction = createTransaction(1);
        monzoTransaction.setMetadata(new MonzoMetadata());

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(monzoTransaction)));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).hasSize(1);
        assertTransactionEqualsMonzoTransaction(transactions.get(0), monzoTransaction);
    }

    @Test
    public void singleTransactionWithExportedTimestamp_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        MonzoTransaction monzoTransaction = createTransaction(1);
        MonzoMetadata metadata = new MonzoMetadata();
        metadata.setCsvExported(ZonedDateTime.parse("2019-01-01T12:00:00.000Z"));
        monzoTransaction.setMetadata(metadata);

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(monzoTransaction)));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void singleTransactionWithNullPotId_getNewTransactions_returnsTransaction() {
        // Given...
        String accountId = "foo";

        MonzoMetadata metadata = new MonzoMetadata();
        metadata.setPotAccountId(null);

        MonzoTransaction transaction = createTransaction(1);
        transaction.setMetadata(metadata);

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(transaction)));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).hasSize(1);
        assertTransactionEqualsMonzoTransaction(transactions.get(0), transaction);
    }

    @Test
    public void singleTransactionWithPotId_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";

        MonzoMetadata metadata = new MonzoMetadata();
        metadata.setPotAccountId("bar");

        MonzoTransaction transaction = createTransaction(1);
        transaction.setMetadata(metadata);

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(transaction)));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void singleTransactionWithDeclineReason_getNewTransaction_returnsEmptyList() {
        // Given...
        String accountId = "foo";

        MonzoTransaction transaction = createTransaction(1);
        transaction.setDeclineReason("INSUFFICIENT_FUNDS");

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(transaction)));

        // When...
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void multipleTransactions_getNewTransactions_filteredCorrectly() {
        // Given...
        String accountId = "foo";
        MonzoTransaction newTransactionWithValue = createTransaction(1);
        MonzoTransaction newTransactionWithZeroValue = createTransaction(0);
        MonzoTransaction exportedTransactionWithValue = createTransaction(1);

        MonzoMetadata metadata = new MonzoMetadata();
        metadata.setCsvExported(ZonedDateTime.parse("2019-01-01T12:00:00.000Z"));
        exportedTransactionWithValue.setMetadata(metadata);

        MonzoTransactions transactions = new MonzoTransactions(Arrays.asList(
                newTransactionWithValue, newTransactionWithZeroValue, exportedTransactionWithValue));

        given(monzoClient.getTransactions(accountId)).willReturn(transactions);

        // When...
        List<Transaction> returnedTransactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(returnedTransactions).hasSize(1);
        assertTransactionEqualsMonzoTransaction(returnedTransactions.get(0), exportedTransactionWithValue);
    }

    @Test
    public void nullTransactions_annotateAsExported_throwsException() {
        // When...
        Throwable throwable = catchThrowable(() -> transactionsService.annotateTransactionsAsExported(null));

        // Then...
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void emptyTransactionsList_annotateAsExported_notCalled() {
        // When...
        transactionsService.annotateTransactionsAsExported(Collections.emptyList());

        // Then...
        verify(monzoClient, never()).setMetadataOnTransaction(anyString(), anyMap());
    }

    @Test
    public void singleTransaction_annotateAsExported_metadataSet() {
        // Given...
        Transaction transaction = new Transaction("foo", ZonedDateTime.parse("2017-01-01T12:00:00.000Z"), "", 1);

        // When...
        transactionsService.annotateTransactionsAsExported(Collections.singletonList(transaction));

        // Then...
        ArgumentCaptor<Map<String, String>> metadataCaptor = ArgumentCaptor.forClass(Map.class);
        verify(monzoClient).setMetadataOnTransaction(eq("foo"), metadataCaptor.capture());

        Map<String, String> metadata = metadataCaptor.getValue();
        assertThat(metadata.get("csv_exported")).isNotBlank();
    }

    @Test
    public void twoTransactions_annotateAsExported_clientCalledTwice() {
        // Given...
        List<Transaction> transactions = Arrays.asList(
                new Transaction("transaction-1", ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "", 1),
                new Transaction("transaction-2", ZonedDateTime.parse("2019-01-01T12:00:01.000Z"), "", 2));

        // When...
        transactionsService.annotateTransactionsAsExported(transactions);

        // Then...
        ArgumentCaptor<Map<String, String>> metadataCaptor = ArgumentCaptor.forClass(Map.class);

        verify(monzoClient).setMetadataOnTransaction(eq("transaction-1"), metadataCaptor.capture());
        Map<String, String> metadata1 = metadataCaptor.getValue();

        verify(monzoClient).setMetadataOnTransaction(eq("transaction-2"), metadataCaptor.capture());
        Map<String, String> metadata2 = metadataCaptor.getValue();

        assertThat(metadata1.get("csv_exported")).isNotBlank();
        assertThat(metadata2.get("csv_exported")).isNotBlank();
    }

    private void assertTransactionEqualsMonzoTransaction(Transaction transaction, MonzoTransaction monzoTransaction) {
        assertThat(transaction.getId()).isEqualTo(monzoTransaction.getId());
        assertThat(transaction.getCreated()).isEqualTo(monzoTransaction.getCreated());
        assertThat(transaction.getDescription()).isEqualTo(monzoTransaction.getDescription());
        assertThat(transaction.getAmount()).isEqualTo(monzoTransaction.getAmount());
    }

    private MonzoTransaction createTransaction(Integer amount) {
        return new MonzoTransaction(ZonedDateTime.parse("2015-01-01T12:00:00.000Z"), "description", amount);
    }
}
