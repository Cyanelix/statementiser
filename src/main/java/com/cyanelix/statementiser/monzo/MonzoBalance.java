package com.cyanelix.statementiser.monzo;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class MonzoBalance {
    private int balanceIncludingFlexibleSavings;

    public int getBalanceIncludingFlexibleSavings() {
        return balanceIncludingFlexibleSavings;
    }

    public void setBalanceIncludingFlexibleSavings(int balanceIncludingFlexibleSavings) {
        this.balanceIncludingFlexibleSavings = balanceIncludingFlexibleSavings;
    }
}
