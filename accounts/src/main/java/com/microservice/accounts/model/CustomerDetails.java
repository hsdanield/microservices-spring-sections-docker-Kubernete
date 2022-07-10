package com.microservice.accounts.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
public class CustomerDetails {

    private Accounts accounts;
    private List<Loans> loans;
    private List<Cards> cards;

}