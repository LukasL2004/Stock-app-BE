package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.WalletDTOMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Repository.WalletRepository;
import com.Calculator.Stock.Services.WalletsService;
import com.Calculator.Stock.dto.WalletDTO;
import com.Calculator.Stock.exception.InsufficientFundsException;
import com.Calculator.Stock.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Transactional
@Service
@AllArgsConstructor
public class WalletServiceImpl implements WalletsService {


    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    public WalletDTO AddFounds(WalletDTO dto, float amount) {
        if(amount <= 0){
            throw new InsufficientFundsException("Sorry the amount must be grater than 0");
        }

        Wallet wallet = walletRepository.findById((long) dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + dto.getId()));


        wallet.setBalance(wallet.getBalance()+amount);
        wallet.setInvestment(wallet.getInvestment()+amount);

        walletRepository.save(wallet);

        return WalletDTOMapper.WalletDTOMapper(wallet);
    }

    @Override
    public  WalletDTO Withdraw(WalletDTO dto, float amount) {

        Wallet wallet = walletRepository.findById((long) dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + dto.getId()));

        if(amount <= 0){
            throw new InsufficientFundsException("Sorry the amount must be grater than 0");
        }
        if(wallet.getBalance() < amount){
            throw new InsufficientFundsException("Sorry the amount you want to withdraw is higher than the balance your currently have $"+wallet.getBalance());
        }
        wallet.setBalance(wallet.getBalance()-amount);

       walletRepository.save(wallet);

        return WalletDTOMapper.WalletDTOMapper(wallet);
    }

    @Override
    public WalletDTO GetBalance(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = user.getWallet();

        return WalletDTOMapper.WalletDTOMapper(wallet);
    }

}
