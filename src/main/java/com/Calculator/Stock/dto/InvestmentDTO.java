package com.Calculator.Stock.dto;

public class InvestmentDTO {

    public InvestmentDTO() {
    }

    private float investment;

    public InvestmentDTO(float investment) {
        this.investment = investment;
    }
    public float getInvestment() {
        return investment;
    }

    public void setInvestment(float investment) {
        this.investment = investment;
    }


}
