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
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class AccountsController {

    private static final Logger logger = LoggerFactory.getLogger(AccountsController.class);

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private AccountsServiceConfig accountsServiceConfig;

    @Autowired
    private LoansFeignClient loansFeignClient;

    @Autowired
    private CardsFeignClient cardsFeignClient;

    @PostMapping("/myAccount")
    @Timed(value = "getAccountDetails.time", description = "Time taken to return Account Details")
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

    @PostMapping("retryAndCircuitBreakGatewayHeaderCustomer")
    @CircuitBreaker(name = "circuitBreakGatewayHeaderCustomer", fallbackMethod = "retryAndCircuitBreakGatewayHeaderCustomerFallBack")
    @Retry(name = "retryForCustomerGatewayHeader", fallbackMethod = "retryAndCircuitBreakGatewayHeaderCustomerFallBack")
    public CustomerDetails retryAndcircuitBreakDetailsForCustomer(@RequestHeader("eazybank-correlation-id") String correlationid, @RequestBody Customer customer) {
        logger.info("retryForCustomerGatewayHeader() method started");
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetailsHeader(correlationid, customer);
        List<Cards> cards = cardsFeignClient.getCardsDetailsHeader(correlationid, customer);
        CustomerDetails customerDetails =  CustomerDetails.builder()
                                                .accounts(accounts)
                                                .loans(loans)
                                                .cards(cards)
                                                .build();
        logger.info("retryForCustomerGatewayHeader() method ended");
        return customerDetails;
    }

    private CustomerDetails retryAndCircuitBreakGatewayHeaderCustomerFallBack(@RequestHeader("eazybank-correlation-id") String correlationid,Customer customer, Throwable t) {
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId());
        List<Loans> loans = loansFeignClient.getLoansDetailsHeader(correlationid,customer);
        return CustomerDetails.builder()
                .accounts(accounts)
                .loans(loans)
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

    @GetMapping("sayHello")
    @RateLimiter(name = "sayHello", fallbackMethod = "sayHelloFallback")
    public String sayHello() {
        Optional<String> podName = Optional.ofNullable(System.getenv("HOSTNAME"));
        return "Hello, Welcome to Microservice Kubernetes cluster from: " + podName.get();
    }

    private String sayHelloFallback(Throwable t) {
        return "Hi, welcome to microservice fallback";
    }

}
