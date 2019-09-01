package com.cyanelix.statementiser.monzo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MonzoTokenReponse {
    @JsonProperty("access_token")
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
