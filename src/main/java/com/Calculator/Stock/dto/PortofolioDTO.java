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
    private float quantity;
    private float averagePrice;
    private User user;
}
