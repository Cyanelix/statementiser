package com.cyanelix.statementiser.controller;

import com.cyanelix.statementiser.domain.MonzoTransaction;
import com.cyanelix.statementiser.domain.MonzoTransactions;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class FilenameGeneratorTest {
    @Test
    public void nullTransactions_generateFilename_throwsException() {
        // When...
        Throwable throwable = catchThrowable(() -> new FilenameGenerator().generateCsvFilename("", null));

        // Then...
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void emptyTransactions_generateFilename_throwsException() {
        // When...
        Throwable throwable = catchThrowable(() ->
                new FilenameGenerator().generateCsvFilename("description", new MonzoTransactions(Collections.emptyList())));

        // Then...
        assertThat(throwable).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    public void nullAccountDescription_generateFilename_throwsException() {
        // When...
        Throwable throwable = catchThrowable(() ->
                new FilenameGenerator().generateCsvFilename(null, generateTransactions("2019-01-01T12:00:00.000Z")));

        // Then...
        assertThat(throwable).isInstanceOf(NullPointerException.class);
    }

    @Test
    public void emptyAccountDescription_generateFilename_throwsException() {
        // When...
        Throwable throwable = catchThrowable(() ->
                new FilenameGenerator().generateCsvFilename("", generateTransactions("2019-01-01T12:00:00.000Z")));

        // Then...
        assertThat(throwable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void singleTransaction_generateFilename_returnOneDateToSameDate() {
        // Given...
        MonzoTransactions monzoTransactions = generateTransactions("2019-01-01T12:00:00.000Z");

        // When...
        String filename = new FilenameGenerator().generateCsvFilename("description", monzoTransactions);

        // Then...
        assertThat(filename).isEqualTo("description_2019-01-01..2019-01-01.csv");
    }

    @Test
    public void twoTransactions_generateFilename_returnFirstDateToSecondDate() {
        // Given...
        MonzoTransactions monzoTransactions = generateTransactions(
                "2019-01-01T12:00:00.000Z",
                "2019-01-02T12:00:00.000Z");

        // When...
        String filename = new FilenameGenerator().generateCsvFilename("description", monzoTransactions);

        // Then...
        assertThat(filename).isEqualTo("description_2019-01-01..2019-01-02.csv");
    }

    @Test
    public void threeTransactions_generateFilename_returnFirstDateToLastDate() {
        // Given...
        MonzoTransactions monzoTransactions = generateTransactions(
                "2019-01-01T12:00:00.000Z",
                "2019-01-02T12:00:00.000Z",
                "2019-01-03T12:00:00.000Z");

        // When...
        String filename = new FilenameGenerator().generateCsvFilename("description", monzoTransactions);

        // Then...
        assertThat(filename).isEqualTo("description_2019-01-01..2019-01-03.csv");
    }

    private MonzoTransactions generateTransactions(String... createdTimestamps) {
        List<MonzoTransaction> transactions = Stream.of(createdTimestamps)
                .map(timestamp -> new MonzoTransaction(ZonedDateTime.parse(timestamp), "", 0))
                .collect(Collectors.toList());

        return new MonzoTransactions(transactions);
    }
}
