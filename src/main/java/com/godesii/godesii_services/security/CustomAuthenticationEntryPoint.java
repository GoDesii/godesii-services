package com.godesii.godesii_services.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

public final class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final String realmName = "Realm";

    private final HandlerExceptionResolver exceptionResolver;

    public CustomAuthenticationEntryPoint(HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        if (SecurityContextHolder.getContext().getAuthentication() != null &&
                SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            exceptionResolver.resolveException(request, response, null, authException);
            return;
        }

        response.setHeader("WWW-Authenticate", "Basic realm=\"" + realmName + "\"");
        response.setContentType("application/json");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());

    }




}
