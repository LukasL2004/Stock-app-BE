package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Repository.WalletRepository;
import com.Calculator.Stock.Services.UserService;
import com.Calculator.Stock.Services.WalletService;
import com.Calculator.Stock.dto.FoundsRequestDTO;
import com.Calculator.Stock.dto.InvestmentDTO;
import com.Calculator.Stock.dto.WalletResponseDTO;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.Calculator.Stock.Entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {
 private final UserService userService;
 private final UserRepository userRepository;
 private final WalletRepository walletRepository;
 private final WalletService walletService;

    public WalletController(UserService userService, UserRepository userRepository, WalletRepository walletRepository, WalletService walletService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.walletService = walletService;
    }

    @GetMapping("/balance")
    public WalletResponseDTO Balance(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return walletService.SeeBalance(email);
    }

    @GetMapping("/Investment")
    public InvestmentDTO Investment(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return walletService.getInvestment(email);
    }

    @PostMapping("/AddFounds")
    public WalletResponseDTO AddFounds(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FoundsRequestDTO amount) {
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));

        Wallet wallet = user.getWallet();
        if(wallet == null) {
            new RuntimeException("Wallet not found");
        }

        return walletService.AddFounds(wallet,amount.getAmount());
    }

    @PostMapping("/Withdraw")
    public WalletResponseDTO Withdraw(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FoundsRequestDTO amount) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));
        Wallet wallet = user.getWallet();
        if(wallet == null) {
            new RuntimeException("Wallet not found");   
        }
        return walletService.Withdraw(wallet,amount.getAmount());
    }
}
