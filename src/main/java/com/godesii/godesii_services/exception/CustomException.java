package com.godesii.godesii_services.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class CustomException {

    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<String> handleException(Exception e) {
        System.out.println("T");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

    }
}
