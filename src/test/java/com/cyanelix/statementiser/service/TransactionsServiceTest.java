package com.cyanelix.statementiser.service;

import com.cyanelix.statementiser.client.MonzoClient;
import com.cyanelix.statementiser.domain.MonzoTransaction;
import com.cyanelix.statementiser.domain.MonzoTransactions;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.context.TestExecutionListeners;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

public class TransactionsServiceTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

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
        List<MonzoTransaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void nullTransactionsList_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(null));

        // When...
        List<MonzoTransaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void emptyTransactionsList_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.emptyList()));

        // When...
        List<MonzoTransaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void singleTransactionWithZeroValue_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(createTransaction(0))));

        // When...
        List<MonzoTransaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).isEmpty();
    }

    @Test
    public void singleTransaction_getNewTransactions_returnsSingleTransaction() {
        // Given...
        String accountId = "foo";
        MonzoTransaction transaction = createTransaction(1);
        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(transaction)));

        // When...
        List<MonzoTransaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).containsExactly(transaction);
    }

    @Test
    public void singleTransactionWithEmptyMetadata_getNewTransactions_returnsTransaction() {
        // Given...
        String accountId = "foo";
        MonzoTransaction transaction = createTransaction(1);
        transaction.setMetadata(Collections.emptyMap());

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(transaction)));

        // When...
        List<MonzoTransaction> transactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(transactions).containsExactly(transaction);
    }

    @Test
    public void singleTransactionWithExportedTimestamp_getNewTransactions_returnsEmptyList() {
        // Given...
        String accountId = "foo";
        MonzoTransaction transaction = createTransaction(1);
        transaction.setMetadata(Collections.singletonMap("csv-exported", "2019-01-01T12:00:00.000Z"));

        given(monzoClient.getTransactions(accountId)).willReturn(new MonzoTransactions(Collections.singletonList(transaction)));

        // When...
        List<MonzoTransaction> transactions = transactionsService.getNewTransactions(accountId);

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
        exportedTransactionWithValue.setMetadata(Collections.singletonMap("csv-exported", "2019-01-01T12:00:00.000Z"));

        MonzoTransactions transactions = new MonzoTransactions(Arrays.asList(
                newTransactionWithValue, newTransactionWithZeroValue, exportedTransactionWithValue));

        given(monzoClient.getTransactions(accountId)).willReturn(transactions);

        // When...
        List<MonzoTransaction> returnedTransactions = transactionsService.getNewTransactions(accountId);

        // Then...
        assertThat(returnedTransactions).containsExactly(newTransactionWithValue);
    }

    private MonzoTransaction createTransaction(Integer amount) {
        return new MonzoTransaction(ZonedDateTime.parse("2015-01-01T12:00:00.000Z"), "description", amount);
    }
}
