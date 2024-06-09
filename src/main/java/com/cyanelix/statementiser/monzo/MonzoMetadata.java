package com.cyanelix.statementiser.monzo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.ZonedDateTime;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MonzoMetadata {
    private ZonedDateTime csvExported;
    private String potAccountId;

    public ZonedDateTime getCsvExported() {
        return csvExported;
    }

    public void setCsvExported(ZonedDateTime csvExported) {
        this.csvExported = csvExported;
    }

    public String getPotAccountId() {
        return potAccountId;
    }

    public void setPotAccountId(String potAccountId) {
        this.potAccountId = potAccountId;
    }
}
