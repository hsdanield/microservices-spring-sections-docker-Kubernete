package com.microservice.loans.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestContextFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

        RequestContextHolder.getContext().setCorrelationId(httpServletRequest.getHeader(RequestContext.CORREELATION_ID));

        logger.debug("Loans Service incoming Correlation id: {}", RequestContextHolder.getContext().getCorrelationId());
        filterChain.doFilter(httpServletRequest, servletResponse);
    }

}
