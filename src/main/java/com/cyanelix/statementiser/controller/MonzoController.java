package com.cyanelix.statementiser.controller;

import com.cyanelix.statementiser.client.MonzoClient;
import com.cyanelix.statementiser.converter.BalanceCalculator;
import com.cyanelix.statementiser.converter.MonzoTransactionsToCsvConverter;
import com.cyanelix.statementiser.domain.*;
import com.cyanelix.statementiser.state.MonzoTokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MonzoController {
    private final MonzoClient monzoClient;
    private final BalanceCalculator balanceCalculator;
    private final FilenameGenerator filenameGenerator;
    private final MonzoTokenHolder monzoTokenHolder;

    @Autowired
    public MonzoController(MonzoClient monzoClient, BalanceCalculator balanceCalculator, FilenameGenerator filenameGenerator, MonzoTokenHolder monzoTokenHolder) {
        this.monzoClient = monzoClient;
        this.balanceCalculator = balanceCalculator;
        this.filenameGenerator = filenameGenerator;
        this.monzoTokenHolder = monzoTokenHolder;
    }

    @GetMapping
    public String getAccountsList(@RequestParam String code, Model model) {
        MonzoTokenReponse monzoTokenReponse = monzoClient.exchangeAuthorisationCode(code);
        monzoTokenHolder.setTokenReponse(monzoTokenReponse);

        List<MonzoAccount> accounts = monzoClient.getAccounts().getAccounts().stream()
                .filter(account -> !account.isClosed())
                .collect(Collectors.toList());
        model.addAttribute("accounts", accounts);

        return "accounts-list";
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<String> getCsv(@PathVariable("accountId") String accountId) {
        MonzoTransactions transactions = monzoClient.getTransactions(accountId);
        MonzoBalance monzoBalance = monzoClient.getBalance(accountId);

        MonzoTransactions transactionsWithBalances = balanceCalculator.calculateBalances(
                monzoBalance.getBalance(), transactions);

        String csv = new MonzoTransactionsToCsvConverter().convert(transactionsWithBalances);

        String filename = filenameGenerator.generateCsvFilename(getAccountById(accountId).getDescription(), transactionsWithBalances);

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }

    private MonzoAccount getAccountById(String accountId) {
        return monzoClient.getAccounts().getAccounts().stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No account found with ID " + accountId));
    }
}
