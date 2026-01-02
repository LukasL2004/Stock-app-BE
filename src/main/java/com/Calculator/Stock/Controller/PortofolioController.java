package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.PortofolioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/portofolio")
public class PortofolioController {

    private final PortofolioService portofolioService;

    @PostMapping
    public PortofolioDTO addToPortfolio(@RequestBody PortofolioDTO dto) {
        return portofolioService.addToPortfolio(dto);
    }
}
