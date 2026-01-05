package com.Calculator.Stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BuyStockDTO {
    private Long user_id;
    private String symbol;
    private float currentPrice;
    private float amountToInvest;
}
