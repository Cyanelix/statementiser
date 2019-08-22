package com.cyanelix.statementiser.state;

import com.cyanelix.statementiser.domain.MonzoTokenReponse;
import org.springframework.stereotype.Component;

@Component
public class MonzoTokenHolder {
    private MonzoTokenReponse tokenReponse;

    public String getAccessToken() {
        return tokenReponse.getAccessToken();
    }

    public MonzoTokenReponse getTokenReponse() {
        return tokenReponse;
    }

    public void setTokenReponse(MonzoTokenReponse tokenReponse) {
        this.tokenReponse = tokenReponse;
    }
}
