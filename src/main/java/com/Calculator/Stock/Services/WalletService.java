package com.Calculator.Stock.Services;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Repository.WalletRepository;
import com.Calculator.Stock.dto.WalletResponseDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    private final WalletRepository walletRepository;
private final UserRepository userRepository;




    public WalletResponseDTO converToDTO(Wallet wallet) {
        return new WalletResponseDTO(wallet.getBalance());
    }

    public WalletResponseDTO AddFounds(Wallet wallet,float amount) {
        if(amount <=0){
            throw new RuntimeException("Amount must be greater than 0");
        }

        wallet.setBalance(wallet.getBalance() + amount);

        return converToDTO(walletRepository.save(wallet));
    }

    public WalletResponseDTO Withdraw(Wallet wallet, float amount) {
        if(wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        return converToDTO(walletRepository.save(wallet));
    }

    public WalletResponseDTO SeeBalance(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = user.getWallet();
        if(wallet == null){
            throw new RuntimeException("Wallet not found");
        }

        return new WalletResponseDTO(wallet.getBalance());


    }

}
