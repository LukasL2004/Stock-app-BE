package com.Calculator.Stock.exception;


public class WrongUserCredentials extends RuntimeException {
    public WrongUserCredentials(String message) {
        super(message);
    }
}
