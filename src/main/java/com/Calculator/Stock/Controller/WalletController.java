package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.WalletDTOMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.WalletsService;
import com.Calculator.Stock.dto.FoundsRequestDTO;
import com.Calculator.Stock.dto.WalletDTO;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.Calculator.Stock.Entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/wallet")
public class WalletController {
 private final UserRepository userRepository;
 private final WalletsService walletService;



    @GetMapping("/balance")
    public WalletDTO Balance(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return walletService.GetBalance(email);
    }

    @PostMapping("/AddFounds")
    public WalletDTO AddFounds(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FoundsRequestDTO amount) {
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));

        Wallet wallet = user.getWallet();
        WalletDTO mappedWallet = WalletDTOMapper.WalletDTOMapper(wallet);

        return walletService.AddFounds(mappedWallet,amount.getAmount());
    }

    @PostMapping("/Withdraw")
    public WalletDTO Withdraw(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FoundsRequestDTO amount) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));
        Wallet wallet = user.getWallet();
        WalletDTO mappedWallet = WalletDTOMapper.WalletDTOMapper(wallet);

        return walletService.Withdraw(mappedWallet,amount.getAmount());
    }
}
