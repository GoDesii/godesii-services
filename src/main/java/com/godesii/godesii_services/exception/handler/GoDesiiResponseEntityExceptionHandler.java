package com.godesii.godesii_services.exception.handler;

import com.godesii.godesii_services.common.APIError;
import io.jsonwebtoken.SignatureException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GoDesiiResponseEntityExceptionHandler  {


    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.NOT_FOUND,"Data Not Found!" , ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<Object> handleBadRequestException(IllegalArgumentException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {AuthenticationException.class, InsufficientAuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = { io.jsonwebtoken.SignatureException.class, SignatureException.class})
    public ResponseEntity<Object> handleAllSException(Exception ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {NoResourceFoundException.class})
    public ResponseEntity<Object> handleNoHandlerException(NoResourceFoundException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }
}
