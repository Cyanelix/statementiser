package com.cyanelix.statementiser.monzo;

import org.junit.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class MonzoTransactionTest {
    @Test
    public void noMetadata_hasBeenExported_true() {
        // Given...
        MonzoTransaction monzoTransaction = new MonzoTransaction();
        monzoTransaction.setMetadata(null);

        // When...
        boolean exported = monzoTransaction.hasBeenExported();

        // Then...
        assertThat(exported).isFalse();
    }

    @Test
    public void noCsvExportedMetadata_hasBeenExported_true() {
        // Given...
        MonzoTransaction monzoTransaction = new MonzoTransaction();
        monzoTransaction.setMetadata(new MonzoMetadata());

        // When...
        boolean exported = monzoTransaction.hasBeenExported();

        // Then...
        assertThat(exported).isFalse();
    }

    @Test
    public void csvExportedMetadataSet_hasBeenExported_false() {
        // Given...
        MonzoMetadata metadata = new MonzoMetadata();
        metadata.setCsvExported(ZonedDateTime.parse("2019-01-01T12:00:00.000Z"));

        MonzoTransaction monzoTransaction = new MonzoTransaction();
        monzoTransaction.setMetadata(metadata);

        // When...
        boolean exported = monzoTransaction.hasBeenExported();

        // Then...
        assertThat(exported).isTrue();
    }

    @Test
    public void noMetadata_isPotTransaction_false() {
        // Given...
        MonzoTransaction monzoTransaction = new MonzoTransaction();
        monzoTransaction.setMetadata(null);

        // When...
        boolean potTransaction = monzoTransaction.isPotTransaction();

        // Then...
        assertThat(potTransaction).isFalse();
    }

    @Test
    public void nullPotId_isPotTransaction_false() {
        // Given...
        MonzoTransaction monzoTransaction = new MonzoTransaction();
        monzoTransaction.setMetadata(new MonzoMetadata());

        // When...
        boolean potTransaction = monzoTransaction.isPotTransaction();

        // Then...
        assertThat(potTransaction).isFalse();
    }

    @Test
    public void emptyPotId_isPotTransaction_false() {
        // Given...
        MonzoMetadata metadata = new MonzoMetadata();
        metadata.setPotAccountId("");

        MonzoTransaction monzoTransaction = new MonzoTransaction();
        monzoTransaction.setMetadata(metadata);

        // When...
        boolean potTransaction = monzoTransaction.isPotTransaction();

        // Then...
        assertThat(potTransaction).isFalse();
    }

    @Test
    public void potIdSet_isPotTransaction_true() {
        // Given...
        MonzoMetadata metadata = new MonzoMetadata();
        metadata.setPotAccountId("foo");

        MonzoTransaction monzoTransaction = new MonzoTransaction();
        monzoTransaction.setMetadata(metadata);

        // When...
        boolean potTransaction = monzoTransaction.isPotTransaction();

        // Then...
        assertThat(potTransaction).isTrue();
    }
}
