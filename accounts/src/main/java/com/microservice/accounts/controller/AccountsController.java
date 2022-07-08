package com.microservice.accounts.controller;

import com.microservice.accounts.model.Accounts;
import com.microservice.accounts.model.Customer;
import com.microservice.accounts.repository.AccountsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountsController {

    @Autowired
    private AccountsRepository accountsRepository;

    @PostMapping("/myAccount")
    public Accounts getAccountDetails(@RequestBody Customer customer) {
        return accountsRepository.findByCustomerId(customer.getCustomerId());
    }

}
