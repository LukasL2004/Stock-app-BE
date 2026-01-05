package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.BuyStockDTO;
import com.Calculator.Stock.dto.PortofolioDTO;
import com.Calculator.Stock.dto.SellStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portofolio")
public class PortofolioController {

    private final PortofolioService portofolioService;
    private final UserRepository userRepository;
    PortofolioDTOMapper portofolioDTOMapper;

    @PostMapping("/buy")
    public PortofolioDTO addToPortfolio(@AuthenticationPrincipal UserDetails userDetails, @RequestBody BuyStockDTO buyStockDTO ) {

       String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        buyStockDTO.setUser_id(user.getId());

        return portofolioService.addToPortfolio(buyStockDTO);
    }@PostMapping("/sell")
    public PortofolioDTO SellFromPortofolio(@AuthenticationPrincipal UserDetails userDetails, @RequestBody SellStockDTO sellStockDTO ) {

       String email = userDetails.getUsername();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        sellStockDTO.setUser_id(user.getId());

        return portofolioService.SellToPortfolio(sellStockDTO);
    }
}
