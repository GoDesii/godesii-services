package com.godesii.godesii_services.exception.handler;

import com.godesii.godesii_services.common.APIError;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}
