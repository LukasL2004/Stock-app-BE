package com.Calculator.Stock.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class SellStockDTO {

    private long user_id;
    @NotBlank(message = "You must select the stock you want to sell!")
    private String symbol;
    @Positive(message = "The current price must be greater than 0!")
    private float currentPrice;
    @Positive(message = "The amount you want to withdraw must be greater than 0!")
    private float withdrawAmount;
}
