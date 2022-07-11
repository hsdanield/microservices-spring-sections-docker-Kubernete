package com.microservice.accounts.service;

import com.microservice.accounts.model.Cards;
import com.microservice.accounts.model.Customer;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("cards")
public interface CardsFeignClient {

    @RequestMapping(method = RequestMethod.POST, value = "myCards", consumes = "application/json")
    List<Cards> getCardsDetails(@RequestBody Customer customer);

    @RequestMapping(method = RequestMethod.POST, value = "myCardsHeader", consumes = "application/json")
    List<Cards> getCardsDetailsHeader(@RequestHeader("eazybank-correlation-id") String correlationid, @RequestBody Customer customer);


}
