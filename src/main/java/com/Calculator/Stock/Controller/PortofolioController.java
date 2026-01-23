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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portofolio")
public class PortofolioController {

    private final PortofolioService portofolioService;
    private final UserRepository userRepository;
    PortofolioDTOMapper portofolioDTOMapper;

    @PostMapping("/buy")
    public ResponseEntity<String> addToPortfolio(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BuyStockDTO buyStockDTO ) {

       String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        buyStockDTO.setUser_id(user.getId());
        portofolioService.addToPortfolio(buyStockDTO);
        return ResponseEntity.ok("Your purchase was a success");
    }@PostMapping("/sell")
    public ResponseEntity<String> SellFromPortofolio(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SellStockDTO sellStockDTO ) {

       String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        sellStockDTO.setUser_id(user.getId());
        portofolioService.SellToPortfolio(sellStockDTO);
        return ResponseEntity.ok("The withdraw was a success");
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
