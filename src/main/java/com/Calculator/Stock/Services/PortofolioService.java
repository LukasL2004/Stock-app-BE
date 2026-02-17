package com.Calculator.Stock.Services;

import com.Calculator.Stock.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface PortofolioService {

    PortofolioDTO addToPortfolio(BuyStockDTO buyStockDTO);
    PortofolioDTO SellToPortfolio(SellStockDTO sellStockDTO);
    PortofolioDTO GetPortofolio(String symbol);
    TotalDTO getTotal();
    DailyProfitDTO getDailyProfit(String symbol);


}
