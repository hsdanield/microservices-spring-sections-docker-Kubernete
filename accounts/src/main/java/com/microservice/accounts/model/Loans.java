package com.microservice.accounts.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class Loans {

    private Long loanNumber;
    private Long customerId;
    private LocalDate startDt;
    private String loanType;
    private Long totalLoan;
    private Long amountPaid;
    private Long outstandingAmount;
    private String createDt;

}