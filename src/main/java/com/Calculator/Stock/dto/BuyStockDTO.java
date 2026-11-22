package com.Calculator.Stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BuyStockDTO {
    private Long user_id;
    @NotBlank(message = "You must select the stock you want to buy!")
    private String symbol;
    @Positive(message = "The current price must be greater than 0!")
    private float currentPrice;
    @Positive(message = "The amount you want to invest must be greater than 0!")
    private float amountToInvest;
}
