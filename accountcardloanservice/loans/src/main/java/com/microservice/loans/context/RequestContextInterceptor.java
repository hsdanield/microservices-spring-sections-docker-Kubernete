package com.microservice.loans.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class RequestContextInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextInterceptor.class);


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        System.out.println("Testing RequestContextInterceptor");
        HttpHeaders headers = request.getHeaders();
        headers.add(RequestContext.CORREELATION_ID, RequestContextHolder.getContext().getCorrelationId());

        return execution.execute(request, body);
    }
}
