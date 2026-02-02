package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.WalletDTOMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.WalletsService;
import com.Calculator.Stock.dto.FoundsRequestDTO;
import com.Calculator.Stock.dto.WalletDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.Calculator.Stock.Entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/wallet")
public class WalletController {
 private final UserRepository userRepository;
 private final WalletsService walletService;
 private final WalletDTOMapper walletDTOMapper;
 private final SimpMessagingTemplate messagingTemplate;



    @GetMapping("/balance")
    public WalletDTO Balance(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return walletService.GetBalance(email);
    }

    @PostMapping("/AddFounds")
    public ResponseEntity<Map<String,String>> AddFounds(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FoundsRequestDTO amount) {
        String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));

        Wallet wallet = user.getWallet();
        WalletDTO mappedWallet = walletDTOMapper.WalletDTOMapper(wallet);

        walletService.AddFounds(mappedWallet,amount.getAmount());

        messagingTemplate.convertAndSendToUser(email,"/topic/founds",Map.of("message","Wallet added successfully","status","OK"));

        return ResponseEntity.ok(Map.of("message","Deposit successful"));
    }

    @PostMapping("/Withdraw")
    public ResponseEntity<Map<String,String>> Withdraw(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FoundsRequestDTO amount) {
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));
        Wallet wallet = user.getWallet();
        WalletDTO mappedWallet = walletDTOMapper.WalletDTOMapper(wallet);

        walletService.Withdraw(mappedWallet,amount.getAmount());

        messagingTemplate.convertAndSendToUser(email,"/topic/founds",Map.of("message","Withdraw successfully","status","OK"));

        return ResponseEntity.ok(Map.of("message","Withdraw successful"));
    }
}
