package com.Calculator.Stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class DailyProfitDTO {
    private long user_id;
    private float dailyProfitValue;
    private float dailyProfitPercentage;
    private float totalPortfolioProfitDaily;
    private float totalPortfolioProfitDailyPercentage;
}
