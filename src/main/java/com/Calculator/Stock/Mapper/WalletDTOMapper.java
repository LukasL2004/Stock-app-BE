package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Repository.WalletRepository;
import com.Calculator.Stock.dto.WalletDTO;
import org.springframework.stereotype.Component;
@Component
public class WalletDTOMapper {


    public WalletDTO WalletDTOMapper(Wallet wallet) {
        return new WalletDTO(wallet.getId(), wallet.getBalance(), wallet.getInvestment());
    }

    public static Wallet WalletMapper(WalletDTO dto) {
        return new Wallet(
                dto.getId(),
                dto.getBalance(),
                dto.getInvestment()
        );
    }
}
