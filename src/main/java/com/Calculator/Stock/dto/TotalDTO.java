package com.Calculator.Stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotalDTO {
private Long user_Id;
private float total;
private float investedValue;
private float marketValue;
private float totalProfit;
private float balance;
List<PortfolioChartDTO> portfolioChart;
}
