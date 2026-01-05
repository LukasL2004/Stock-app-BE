package com.Calculator.Stock.dto;


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
    private String symbol;
    private float currentPrice;
    private float withdrawAmount;
}
