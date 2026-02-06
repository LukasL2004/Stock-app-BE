package com.Calculator.Stock.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordDTO {
private String email;
private String password;
private int code;
}
