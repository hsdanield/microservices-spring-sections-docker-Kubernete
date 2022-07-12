package com.microservice.cards.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.microservice.cards.config.CardsServiceConfig;
import com.microservice.cards.model.Cards;
import com.microservice.cards.model.Customer;
import com.microservice.cards.model.Properties;
import com.microservice.cards.repository.CardsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CardsController {

    private static final Logger logger = LoggerFactory.getLogger(CardsController.class);

    @Autowired
    private CardsRepository cardsRepository;

    @Autowired
    private CardsServiceConfig cardsServiceConfig;

    @PostMapping("/myCards")
    public List<Cards> getCardDetails(@RequestBody Customer customer) {
        return cardsRepository.findByCustomerId(customer.getCustomerId());
    }

    @PostMapping("/myCardsHeader")
    public List<Cards> getCardDetailsHeader(@RequestHeader("eazybank-correlation-id") String correlationid, @RequestBody Customer customer) {
        logger.info("getCardDetailsHeader() method started");
        List<Cards> cards = cardsRepository.findByCustomerId(customer.getCustomerId());
        logger.info("getCardDetailsHeader() method ended");
        return cards;
    }

    @GetMapping("/cards/properties")
    public String getPropertyDetails() throws JsonProcessingException {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Properties properties = Properties.builder()
                .msg(cardsServiceConfig.getMsg())
                .buildVersion(cardsServiceConfig.getBuildVersion())
                .mailDetails(cardsServiceConfig.getMailDetails())
                .activeBranches(cardsServiceConfig.getActiveBranches())
                .build();
        return ow.writeValueAsString(properties);
    }

}
