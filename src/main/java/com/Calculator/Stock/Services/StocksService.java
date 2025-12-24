package com.Calculator.Stock.Services;

import com.Calculator.Stock.dto.StocksDTO;
import com.Calculator.Stock.dto.TwelveDataDTO;
import org.springframework.stereotype.Service;

@Service
public interface StocksService {
    TwelveDataDTO getStocks(String symbol);
}
