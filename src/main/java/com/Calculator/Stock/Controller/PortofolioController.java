package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.BuyStockDTO;
import com.Calculator.Stock.dto.PortofolioDTO;
import com.Calculator.Stock.dto.SellStockDTO;
import com.Calculator.Stock.dto.TotalDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portofolio")
public class PortofolioController {

    private final PortofolioService portofolioService;
    private final UserRepository userRepository;
    private final PortofolioDTOMapper portofolioDTOMapper;
    private final SimpMessagingTemplate messagingTemplate;


    @PostMapping("/buy")
    public ResponseEntity<Map<String,String>> addToPortfolio(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BuyStockDTO buyStockDTO ) {

       String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        buyStockDTO.setUser_id(user.getId());
        portofolioService.addToPortfolio(buyStockDTO);
        messagingTemplate.convertAndSendToUser(email,"/topic/user/"+ user.getId() ,Map.of("message","Your purchase was a success" ,"Status" , "Success"));

        return ResponseEntity.ok(Map.of("message","Your purchase was a success"));
    }

    @PostMapping("/sell")
    public ResponseEntity<Map<String,String>> SellFromPortofolio(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SellStockDTO sellStockDTO ) {

       String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        sellStockDTO.setUser_id(user.getId());
        portofolioService.SellToPortfolio(sellStockDTO);

        messagingTemplate.convertAndSendToUser(email,"/topic/user/"+ user.getId() ,Map.of("message","Your sell was a success" ,"Status" , "Success"));

        return ResponseEntity.ok(Map.of("message","The withdraw was a success"));
    }

    @GetMapping("/{symbol}")
    public PortofolioDTO getPortofolio(@AuthenticationPrincipal UserDetails userDetails,@PathVariable String symbol) {
        return portofolioService.GetPortofolio(symbol);
    }
    @GetMapping("/total")
    public TotalDTO getTotal(@AuthenticationPrincipal UserDetails userDetails) {
        return portofolioService.getTotal();
    }
}
