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
    private float price;

    private float open;
    private float high;
    private float low;
    private String volume;
    private String status;
}
