package com.Calculator.Stock.Services;

import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.dto.StocksDTO;
import com.Calculator.Stock.dto.TwelveDataDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface StocksService {
    TwelveDataDTO getStocks(String symbol);

    List<StocksDTO> getStocksValues();
}
