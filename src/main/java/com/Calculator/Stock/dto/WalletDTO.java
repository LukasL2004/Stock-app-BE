package com.Calculator.Stock.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WalletDTO {
    private int id;
    private float balance;
    @NotNull(message = "The amount must be greater than 0! ")
    private float investment;
}
