package com.Calculator.Stock.exception;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter

public class ApiError {
    private String message;
    private int statusCode;
    private LocalDateTime timestamp;

    public ApiError(String message, int statusCode) {
        this.message = message;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now();
    }
}
