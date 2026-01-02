package com.Calculator.Stock.Services;

import com.Calculator.Stock.dto.PortofolioDTO;
import org.springframework.stereotype.Service;

@Service
public interface PortofolioService {

    PortofolioDTO addToPortfolio(PortofolioDTO dto);

}
