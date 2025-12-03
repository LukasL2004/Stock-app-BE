package com.Calculator.Stock.dto;


public class FoundsRequestDTO {


    private Float amount;

    public FoundsRequestDTO(Float amount) {
        this.amount = amount;
    }

    public FoundsRequestDTO() {}


    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
