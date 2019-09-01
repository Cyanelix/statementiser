package com.cyanelix.statementiser.controller;

import com.cyanelix.statementiser.client.MonzoClient;
import com.cyanelix.statementiser.converter.BalanceCalculator;
import com.cyanelix.statementiser.converter.TransactionsToCsvConverter;
import com.cyanelix.statementiser.domain.Transaction;
import com.cyanelix.statementiser.monzo.MonzoAccount;
import com.cyanelix.statementiser.monzo.MonzoBalance;
import com.cyanelix.statementiser.monzo.MonzoTokenReponse;
import com.cyanelix.statementiser.service.TransactionsService;
import com.cyanelix.statementiser.state.MonzoTokenHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MonzoController implements ApplicationContextAware {
    private final MonzoClient monzoClient;
    private final TransactionsService transactionsService;
    private final BalanceCalculator balanceCalculator;
    private final FilenameGenerator filenameGenerator;
    private final MonzoTokenHolder monzoTokenHolder;

    private ApplicationContext applicationContext;

    @Autowired
    public MonzoController(MonzoClient monzoClient, TransactionsService transactionsService, BalanceCalculator balanceCalculator, FilenameGenerator filenameGenerator, MonzoTokenHolder monzoTokenHolder) {
        this.monzoClient = monzoClient;
        this.transactionsService = transactionsService;
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
    public ResponseEntity<String> getNewTransactions(@PathVariable("accountId") String accountId) {
        List<Transaction> transactions = transactionsService.getNewTransactions(accountId);

        MonzoBalance monzoBalance = monzoClient.getBalance(accountId);

        List<Transaction> transactionsWithBalances = balanceCalculator.calculateBalances(
                monzoBalance.getBalanceIncludingFlexibleSavings(), transactions);

        String csv = new TransactionsToCsvConverter().convert(transactionsWithBalances);

        String filename = filenameGenerator.generateCsvFilename(
                getAccountById(accountId).getDescription(), transactionsWithBalances);

        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

        return new ResponseEntity<>(csv, headers, HttpStatus.OK);
    }

    @GetMapping("/shutdown")
    public void shutdown() {
        // TODO: Maybe start this in a thread with a few seconds delay, and return a goodbye page.
        ((ConfigurableApplicationContext) applicationContext).close();
    }

    private MonzoAccount getAccountById(String accountId) {
        return monzoClient.getAccounts().getAccounts().stream()
                .filter(account -> account.getId().equals(accountId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No account found with ID " + accountId));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
