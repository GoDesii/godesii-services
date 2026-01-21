package com.godesii.godesii_services.exception.handler;

import com.godesii.godesii_services.common.APIError;
import com.godesii.godesii_services.exception.ResourceNotFoundException;
import io.jsonwebtoken.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GoDesiiResponseEntityExceptionHandler {

    public static final Logger LOGGER = LoggerFactory.getLogger(GoDesiiResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {
            IllegalStateException.class,
            ResourceNotFoundException.class
    })
    public ResponseEntity<Object> handleNotFoundException(RuntimeException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getStackTrace(), request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {
            IllegalArgumentException.class,
            InvalidDataAccessApiUsageException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<Object> handleBadRequestException(RuntimeException ex, WebRequest request) {
        LOGGER.error(ex.getMessage(), ex);
        APIError apiError = new APIError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getStackTrace(), request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {
            AuthenticationException.class,
            InsufficientAuthenticationException.class
    })
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.UNAUTHORIZED, ex.getMessage(), ex.getStackTrace(), request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = {
            io.jsonwebtoken.SignatureException.class,
            SignatureException.class
    })
    public ResponseEntity<Object> handleAllSException(Exception ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getStackTrace(),
                request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleAllException(Exception ex, WebRequest request) {
        LOGGER.error(ex.getMessage(), ex);
        APIError apiError = new APIError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), ex.getStackTrace(),
                request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }

    @ExceptionHandler(value = { NoResourceFoundException.class })
    public ResponseEntity<Object> handleNoHandlerException(NoResourceFoundException ex, WebRequest request) {
        APIError apiError = new APIError(HttpStatus.NOT_FOUND, ex.getMessage(), ex.getStackTrace(), request);
        return ResponseEntity
                .status(apiError.getHttpStatus())
                .body(apiError);
    }
}
