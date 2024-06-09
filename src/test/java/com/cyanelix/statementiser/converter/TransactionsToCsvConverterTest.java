package com.cyanelix.statementiser.converter;

import com.cyanelix.statementiser.domain.Transaction;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class TransactionsToCsvConverterTest {
    @Test
    public void nullTransactions_throwsException() {
        // When...
        Throwable throwable = catchThrowable(() -> new TransactionsToCsvConverter().convert(null));

        // Then...
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void singleTransaction_toCsv() {
        // Given...
        Transaction transaction = createTransaction("2019-02-03T04:05:06.000Z", "Test transaction", 100, 100);
        List<Transaction> transactions = Collections.singletonList(transaction);

        // When...
        String csv = new TransactionsToCsvConverter().convert(transactions);

        // Then...
        assertThat(csv).isEqualTo("\"03/02/2019\",\"Test transaction\",\"1.00\",\"1.00\"");
    }

    @Test
    public void multipleTransactions_toCsv() {
        // Given...
        List<Transaction> transactions = Arrays.asList(
                createTransaction("2019-03-04T05:06:07.000Z", "First transaction", 100, 100),
                createTransaction("2019-04-05T06:07:09.999Z", "Second transaction", 399, 499));

        // When...
        String csv = new TransactionsToCsvConverter().convert(transactions);

        // Then...
        assertThat(csv).isEqualTo(
                "\"04/03/2019\",\"First transaction\",\"1.00\",\"1.00\"\n" +
                        "\"05/04/2019\",\"Second transaction\",\"3.99\",\"4.99\"");
    }

    private Transaction createTransaction(String createdTimestamp, String description, int amount, int calculatedBalance) {
        return new Transaction("foo",
                ZonedDateTime.parse(createdTimestamp),
                description,
                amount,
                calculatedBalance);
    }
}
