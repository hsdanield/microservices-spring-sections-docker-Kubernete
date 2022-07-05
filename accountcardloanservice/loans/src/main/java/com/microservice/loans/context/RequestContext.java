package com.microservice.loans.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class RequestContext {
    public static final String CORREELATION_ID = "ms-correlation-id";

    private String correlationId = new String();

}
