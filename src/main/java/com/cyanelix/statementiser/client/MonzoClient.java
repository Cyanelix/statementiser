package com.cyanelix.statementiser.client;

import com.cyanelix.statementiser.config.MonzoClientConfig;
import com.cyanelix.statementiser.domain.*;
import com.cyanelix.statementiser.state.MonzoTokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class MonzoClient {
    private final RestTemplate restTemplate;
    private final MonzoTokenHolder monzoTokenHolder;
    private final MonzoClientConfig monzoClientConfig;

    @Autowired
    public MonzoClient(RestTemplate restTemplate, MonzoTokenHolder monzoTokenHolder, MonzoClientConfig monzoClientConfig) {
        this.restTemplate = restTemplate;
        this.monzoTokenHolder = monzoTokenHolder;
        this.monzoClientConfig = monzoClientConfig;
    }

    public void requestAuthorisationCode() {
        MultiValueMap<String, String> formMap = new LinkedMultiValueMap<>();
        formMap.add("email", monzoClientConfig.getEmail());
        formMap.add("redirect_uri", monzoClientConfig.getRedirectUri());
        formMap.add("response_type", "code");
        formMap.add("client_id", monzoClientConfig.getClientId());

        HttpHeaders headers = createHeadersWithFormContentType();
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(formMap, headers);

        restTemplate.postForEntity("https://api.monzo.com/oauth2/authorize", formEntity, String.class);
    }

    public MonzoTokenReponse exchangeAuthorisationCode(String authorisationCode) {
        MultiValueMap<String, String> formMap = new LinkedMultiValueMap<>();
        formMap.add("grant_type", "authorization_code");
        formMap.add("client_id", monzoClientConfig.getClientId());
        formMap.add("client_secret", monzoClientConfig.getClientSecret());
        formMap.add("redirect_uri", monzoClientConfig.getRedirectUri());
        formMap.add("code", authorisationCode);

        HttpHeaders headers = createHeadersWithFormContentType();
        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(formMap, headers);

        return restTemplate.postForObject("https://api.monzo.com/oauth2/token", formEntity, MonzoTokenReponse.class);
    }

    public MonzoAccounts getAccounts() {
        return getWithBearerToken("https://api.monzo.com/accounts", MonzoAccounts.class);
    }

    public MonzoTransactions getTransactions(String accountId) {
        return getWithBearerToken("https://api.monzo.com/transactions?account_id=" + accountId, MonzoTransactions.class);
    }

    public MonzoBalance getBalance(String accountId) {
        return getWithBearerToken("https://api.monzo.com/balance?account_id=" + accountId, MonzoBalance.class);
    }

    public void setMetadataOnTransaction(String transactionId, Map<String, String> metadata) {
        MultiValueMap<String, String> formMap = new LinkedMultiValueMap<>();
        metadata.forEach((key, value) -> formMap.add("metadata[" + key + "]", value));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + monzoTokenHolder.getAccessToken());

        HttpEntity<MultiValueMap<String, String>> formEntity = new HttpEntity<>(formMap, headers);

        MonzoTransaction monzoTransaction = restTemplate.patchForObject("https://api.monzo.com/transactions/" + transactionId, formEntity, MonzoTransaction.class);
        System.out.println(monzoTransaction.getDescription());
    }

    private <T> T getWithBearerToken(String url, Class<T> returnType) {
        return exchangeWithBearerToken(url, HttpMethod.GET, returnType);
    }

    private <T> T exchangeWithBearerToken(String url, HttpMethod method, Class<T> returnType) {
        HttpHeaders headers = createHeadersWithBearerToken();

        HttpEntity<Void> httpEntity = new HttpEntity<>(null, headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(url, method, httpEntity, returnType);
        return responseEntity.getBody();
    }

    private HttpHeaders createHeadersWithFormContentType() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");
        return headers;
    }

    private HttpHeaders createHeadersWithBearerToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + monzoTokenHolder.getAccessToken());
        return headers;
    }
}
