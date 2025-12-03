package com.Calculator.Stock.dto;

public class WalletResponseDTO {

    private float balance;

    public WalletResponseDTO(float balance) {

        this.balance = balance;
    }



    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}
