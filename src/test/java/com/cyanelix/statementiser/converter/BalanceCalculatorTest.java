package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class BalanceCalculatorTest {
    private final BalanceCalculator balanceCalculator = new BalanceCalculator();

    @Test
    public void nullTransactions_calculateBalances_throwsNullPointerException() {
        // When...
        Throwable throwable = catchThrowable(() -> balanceCalculator.calculateBalances(1, null));

        // Then...
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void emptyTransactions_calculateBalances_returnsEmptyTransactions() {
        // When...
        List<Transaction> outputTransactions = balanceCalculator.calculateBalances(1, Collections.emptyList());

        // Then...
        assertThat(outputTransactions).isEmpty();
    }

    @Test
    public void singleMonzoTransaction_calculateBalance_returnsCorrectBalance() {
        // Given...
        List<Transaction> inputTransactions = Collections.singletonList(
                new Transaction("foo", ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "Description", 200));

        // When...
        List<Transaction> transactions = balanceCalculator.calculateBalances(200, inputTransactions);

        // Then...
        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getRunningBalance()).isEqualTo(200);
    }

    @Test
    public void twoPositiveMonzoTransactions_calculateBalance_correctBalanceForEachTransaction() {
        // Given...
        List<Transaction> inputTransactions = Arrays.asList(
                new Transaction("foo", ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "First", 100),
                new Transaction("bar", ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", 200));

        // When...
        List<Transaction> transactions = balanceCalculator.calculateBalances(300, inputTransactions);

        // Then...
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getRunningBalance()).isEqualByComparingTo(100);
        assertThat(transactions.get(1).getRunningBalance()).isEqualByComparingTo(300);
    }

    @Test
    public void onePositiveOneNegativeTransaction_calculateBalance_correctBalanceForEachTransaction() {
        // Given...
        List<Transaction> inputTransactions = Arrays.asList(
                new Transaction("foo", ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "First", 100),
                new Transaction("bar", ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", -100));

        // When...
        List<Transaction> transactions = balanceCalculator.calculateBalances(0, inputTransactions);

        // Then...
        assertThat(transactions).hasSize(2);
        assertThat(transactions.get(0).getRunningBalance()).isEqualByComparingTo(100);
        assertThat(transactions.get(1).getRunningBalance()).isEqualByComparingTo(0);
    }

    @Test
    public void threeTransactions_calculateBalance_correctBalanceForEachTransaction() {
        // Given...
        List<Transaction> inputTransactions = Arrays.asList(
                new Transaction("foo", ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "First", 100),
                new Transaction("bar", ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", 200),
                new Transaction("baz", ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", 300));

        // When...
        List<Transaction> transactions = balanceCalculator.calculateBalances(600, inputTransactions);

        // Then...
        assertThat(transactions).hasSize(3);
        assertThat(transactions.get(0).getRunningBalance()).isEqualByComparingTo(100);
        assertThat(transactions.get(1).getRunningBalance()).isEqualByComparingTo(300);
        assertThat(transactions.get(2).getRunningBalance()).isEqualByComparingTo(600);
    }
}
