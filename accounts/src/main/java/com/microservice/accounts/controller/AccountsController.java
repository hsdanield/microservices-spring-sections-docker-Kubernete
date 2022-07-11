package com.microservice.accounts.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.microservice.accounts.config.AccountsServiceConfig;
import com.microservice.accounts.model.*;
import com.microservice.accounts.repository.AccountsRepository;
import com.microservice.accounts.service.CardsFeignClient;
import com.microservice.accounts.service.LoansFeignClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountsController {

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountsServiceConfig accountsServiceConfig;

    @Autowired
    private LoansFeignClient loansFeignClient;

    @Autowired
    private CardsFeignClient cardsFeignClient;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        return accountsRepository.findByCustomerId(customer.getCustomerId());
    }

    @GetMapping("/account/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = Properties.builder()
                .msg(accountsServiceConfig.getMsg())
                .buildVersion(accountsServiceConfig.getBuildVersion())
                .mailDetails(accountsServiceConfig.getMailDetails())
                .activeBranches(accountsServiceConfig.getActiveBranches())
                .build();
        return ow.writeValueAsString(properties);
    }
    @PostMapping("myCustomerDetails")
    public CustomerDetails myCustomerDetails(@RequestBody Customer customer) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(customer);
        List<Cards> cards = cardsFeignClient.getCardsDetails(customer);
        return CustomerDetails.builder()
                .accounts(accounts)
                .loans(loans)
                .cards(cards)
                .build();
    }
    @PostMapping("circuitBreakDetailsForCustomer")
    @CircuitBreaker(name = "detailsForCustomerSupportApp", fallbackMethod = "myCustomerDetailsFallBack")
    public CustomerDetails myCustomerDetailsCircuitBreak(@RequestBody Customer customer) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(customer);
        List<Cards> cards = cardsFeignClient.getCardsDetails(customer);
        return CustomerDetails.builder()
                .accounts(accounts)
                .loans(loans)
                .cards(cards)
                .build();
    }

    @PostMapping("retryForCustomerDetails")
    @Retry(name = "retryForCustomerDetails", fallbackMethod = "myCustomerDetailsFallBack")
    public CustomerDetails myCustomerDetailsRetry(@RequestBody Customer customer) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(customer);
        List<Cards> cards = cardsFeignClient.getCardsDetails(customer);
        return CustomerDetails.builder()
                .accounts(accounts)
                .loans(loans)
                .cards(cards)
                .build();
    }
    private CustomerDetails myCustomerDetailsFallBack(Customer customer, Throwable t) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetails(customer);
        return CustomerDetails.builder()
                .accounts(accounts)
                .loans(loans)
                .build();
    }

}
