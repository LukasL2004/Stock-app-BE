package com.Calculator.Stock.Services;

import com.Calculator.Stock.dto.WalletDTO;

public interface WalletsService {

    public  WalletDTO AddFounds(WalletDTO dto,float amount);
    public WalletDTO Withdraw(WalletDTO dto,float amount);
    public  WalletDTO GetBalance(String email);

}
