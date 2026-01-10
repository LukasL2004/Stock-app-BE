package com.Calculator.Stock.Services;

import com.Calculator.Stock.dto.BuyStockDTO;
import com.Calculator.Stock.dto.PortofolioDTO;
import com.Calculator.Stock.dto.SellStockDTO;
import org.springframework.stereotype.Service;

@Service
public interface PortofolioService {

    PortofolioDTO addToPortfolio(BuyStockDTO buyStockDTO);
    PortofolioDTO SellToPortfolio(SellStockDTO sellStockDTO);
    PortofolioDTO GetPortofolio(String symbol);

}
