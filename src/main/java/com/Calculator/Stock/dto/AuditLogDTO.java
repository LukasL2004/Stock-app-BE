package com.Calculator.Stock.dto;

import com.Calculator.Stock.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogDTO {
    private Long id;
    private String symbol;
    private float total;
    private String date;
    private float price;
    private float shares;

    private Long User_id;
}
