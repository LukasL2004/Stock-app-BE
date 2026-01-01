package com.Calculator.Stock.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ChartDataDTO {

    private String symbol;
    private String time;
    private String date;
    private float value ;

}
