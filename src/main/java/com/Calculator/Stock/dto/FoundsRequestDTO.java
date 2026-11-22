package com.Calculator.Stock.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FoundsRequestDTO {


    @NotNull(message = "The amount is required")
    @Positive(message = "The amount must be greater than 0")
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
