package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.MonzoTransaction;
import com.cyanelix.statementiser.domain.MonzoTransactions;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class BalanceCalculatorTest {
    private final BalanceCalculator balanceCalculator = new BalanceCalculator();

    @Test
    public void nullMonzoTransactions_calculateBalances_throwsNullPointerException() {
        // When...
        Throwable throwable = catchThrowable(() -> balanceCalculator.calculateBalances(1, null));

        // Then...
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void emptyMonzoTransactions_calculateBalances_returnsEmptyMonzoTransactions() {
        // Given...
        MonzoTransactions inputTransactions = new MonzoTransactions(Collections.emptyList());

        // When...
        MonzoTransactions outputTransactions = balanceCalculator.calculateBalances(1, inputTransactions);

        // Then...
        assertThat(outputTransactions.getTransactions()).isEmpty();
    }

    @Test
    public void singleMonzoTransaction_calculateBalance_returnsCorrectBalance() {
        // Given...
        MonzoTransactions inputTransactions = new MonzoTransactions(Collections.singletonList(
                new MonzoTransaction(ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "Description", 200)));

        // When...
        MonzoTransactions transactions = balanceCalculator.calculateBalances(200, inputTransactions);

        // Then...
        assertThat(transactions.getTransactions()).hasSize(1);
        assertThat(transactions.getTransactions().get(0).getCalculatedBalance()).isEqualTo(200);
    }

    @Test
    public void twoPositiveMonzoTransactions_calculateBalance_correctBalanceForEachTransaction() {
        // Given...
        MonzoTransactions inputTransactions = new MonzoTransactions(Arrays.asList(
                new MonzoTransaction(ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "First", 100),
                new MonzoTransaction(ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", 200)));

        // When...
        MonzoTransactions transactions = balanceCalculator.calculateBalances(300, inputTransactions);

        // Then...
        assertThat(transactions.getTransactions()).hasSize(2);
        assertThat(transactions.getTransactions().get(0).getCalculatedBalance()).isEqualByComparingTo(100);
        assertThat(transactions.getTransactions().get(1).getCalculatedBalance()).isEqualByComparingTo(300);
    }

    @Test
    public void onePositiveOneNegativeTransaction_calculateBalance_correctBalanceForEachTransaction() {
        // Given...
        MonzoTransactions inputTransactions = new MonzoTransactions(Arrays.asList(
                new MonzoTransaction(ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "First", 100),
                new MonzoTransaction(ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", -100)));

        // When...
        MonzoTransactions transactions = balanceCalculator.calculateBalances(0, inputTransactions);

        // Then...
        assertThat(transactions.getTransactions()).hasSize(2);
        assertThat(transactions.getTransactions().get(0).getCalculatedBalance()).isEqualByComparingTo(100);
        assertThat(transactions.getTransactions().get(1).getCalculatedBalance()).isEqualByComparingTo(0);
    }

    @Test
    public void threeTransactions_calculateBalance_correctBalanceForEachTransaction() {
        // Given...
        MonzoTransactions inputTransactions = new MonzoTransactions(Arrays.asList(
                new MonzoTransaction(ZonedDateTime.parse("2019-01-01T12:00:00.000Z"), "First", 100),
                new MonzoTransaction(ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", 200),
                new MonzoTransaction(ZonedDateTime.parse("2019-01-02T12:01:01.000Z"), "Second", 300)));

        // When...
        MonzoTransactions transactions = balanceCalculator.calculateBalances(600, inputTransactions);

        // Then...
        assertThat(transactions.getTransactions()).hasSize(3);
        assertThat(transactions.getTransactions().get(0).getCalculatedBalance()).isEqualByComparingTo(100);
        assertThat(transactions.getTransactions().get(1).getCalculatedBalance()).isEqualByComparingTo(300);
        assertThat(transactions.getTransactions().get(2).getCalculatedBalance()).isEqualByComparingTo(600);
    }
}
