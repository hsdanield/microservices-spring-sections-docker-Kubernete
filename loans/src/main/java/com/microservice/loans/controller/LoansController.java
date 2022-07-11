package com.microservice.loans.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.microservice.loans.config.LoansServiceConfig;
import com.microservice.loans.model.Customer;
import com.microservice.loans.model.Loans;
import com.microservice.loans.model.Properties;
import com.microservice.loans.repository.LoansRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class LoansController {

    @Autowired
    private LoansRepository loansRepository;

    @Autowired
    private LoansServiceConfig loansServiceConfig;

    @PostMapping("/myLoans")
    public List<Loans> getLoansDetails(@RequestBody Customer customer) {
        System.out.println("Invoking Loans Microservice");
        return loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
    }

    @PostMapping("/myLoansHeader")
    public List<Loans> myLoansHeader(@RequestHeader("eazybank-correlation-id") String correlationid, @RequestBody Customer customer) {
        System.out.println("Invoking Loans Microservice");
        return loansRepository.findByCustomerIdOrderByStartDtDesc(customer.getCustomerId());
    }

    @GetMapping("/loans/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = Properties.builder()
                .msg(loansServiceConfig.getMsg())
                .buildVersion(loansServiceConfig.getBuildVersion())
                .mailDetails(loansServiceConfig.getMailDetails())
                .activeBranches(loansServiceConfig.getActiveBranches())
                .build();
        return ow.writeValueAsString(properties);
    }
}
