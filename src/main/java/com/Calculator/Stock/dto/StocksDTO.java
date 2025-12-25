package com.Calculator.Stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StocksDTO {
    private String id;
    private String symbol;


    private String date;
    private String price;

    private String open;
    private String high;
    private String low;
    private String volume;
}
