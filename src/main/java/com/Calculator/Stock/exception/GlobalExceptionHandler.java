package com.Calculator.Stock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


@ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();

        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


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

@ExceptionHandler
public ResponseEntity<ApiError> WrongCredentials(WrongUserCredentials e){
    ApiError apiError = new ApiError(
            e.getMessage(),
            HttpStatus.UNAUTHORIZED.value()
    );
    return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
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
