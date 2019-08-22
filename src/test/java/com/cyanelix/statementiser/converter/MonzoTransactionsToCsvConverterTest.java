package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.MonzoTransaction;
import com.cyanelix.statementiser.domain.MonzoTransactions;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class MonzoTransactionsToCsvConverterTest {
    @Test
    public void nullTransactions_throwsException() {
        // When...
        Throwable throwable = catchThrowable(() -> new MonzoTransactionsToCsvConverter().convert(null));

        // Then...
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void singleTransaction_toCsv() {
        // Given...
        MonzoTransaction monzoTransaction = createTransaction("2019-02-03T04:05:06.000Z", "Test transaction", 100, 100);

        MonzoTransactions monzoTransactions = new MonzoTransactions(Collections.singletonList(monzoTransaction));

        // When...
        String csv = new MonzoTransactionsToCsvConverter().convert(monzoTransactions);

        // Then...
        assertThat(csv).isEqualTo("\"03/02/2019\",\"Test transaction\",\"1.00\",\"1.00\"");
    }

    @Test
    public void multipleTransactions_toCsv() {
        // Given...
        MonzoTransactions monzoTransactions = new MonzoTransactions(Arrays.asList(
                createTransaction("2019-03-04T05:06:07.000Z", "First transaction", 100, 100),
                createTransaction("2019-04-05T06:07:09.999Z", "Second transaction", 399, 499)
        ));

        // When...
        String csv = new MonzoTransactionsToCsvConverter().convert(monzoTransactions);

        // Then...
        assertThat(csv).isEqualTo(
                "\"04/03/2019\",\"First transaction\",\"1.00\",\"1.00\"\n" +
                        "\"05/04/2019\",\"Second transaction\",\"3.99\",\"4.99\"");
    }

    private MonzoTransaction createTransaction(String createdTimestamp, String description, int amount, int calculatedBalance) {
        return new MonzoTransaction(
                ZonedDateTime.parse(createdTimestamp),
                description,
                amount,
                calculatedBalance);
    }
}
