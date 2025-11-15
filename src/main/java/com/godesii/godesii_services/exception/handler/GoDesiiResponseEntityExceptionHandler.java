package com.godesii.godesii_services.exception.handler;

import com.godesii.godesii_services.common.APIError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GoDesiiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(value = {IllegalStateException.class})
    public ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.NOT_FOUND,"Data Not Found!" , ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {IllegalAccessException.class})
    public ResponseEntity<Object> handleBadRequestException(Exception ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getStackTrace(),request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getStackTrace(),request);
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
}
