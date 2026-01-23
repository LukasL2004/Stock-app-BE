package com.Calculator.Stock.dto;


import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Please enter a valid email!")
    private String email;
    @NotBlank(message = "Please enter a valid password.")
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
