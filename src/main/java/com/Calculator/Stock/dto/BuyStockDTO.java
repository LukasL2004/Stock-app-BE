package com.Calculator.Stock.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BuyStockDTO {
    private Long user_id;
    @NotBlank(message = "You must select the stock you want to buy!")
    private String symbol;
    private float currentPrice;
    @NotNull(message="The amount you want to invest must be grater then 0!")
    private float amountToInvest;
}
