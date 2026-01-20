package com.Calculator.Stock.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

@ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> resourceNotFound(ResourceNotFoundException e) {
    ApiError apiError = new ApiError(
            e.getMessage(),
            HttpStatus.NOT_FOUND.value()
    );
    return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
}

@ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ApiError> insufficientFunds(InsufficientFundsException e) {
    ApiError apiError = new ApiError(
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value()
    );
    return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
}

@ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> globalException(Exception e) {
    ApiError apiError = new ApiError(
            "Internal error appeared " + e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
    );
    return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
}

}
