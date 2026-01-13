package com.Calculator.Stock.dto;


import com.Calculator.Stock.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PortofolioDTO {

    private Long id;
    private String symbol;
    private float averagePrice;
    private float amountOwned;
    private float shares;
    private float profit;
    private float total;
    private Long userId;
}
