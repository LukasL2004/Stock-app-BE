package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Services.StocksService;
import com.Calculator.Stock.dto.ChartDataDTO;
import com.Calculator.Stock.dto.StocksDTO;
import com.Calculator.Stock.dto.TwelveDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StocksController {

    private final StocksService stocksService;

    @PostMapping
    public TwelveDataDTO getStocks(@RequestBody String symbol) {
        return stocksService.getStocks(symbol);
    }

    @GetMapping("/values")
    public List<StocksDTO> getStocksValues() {
        return stocksService.getStocksValues();
    }
    @GetMapping("/charts")
    public List<ChartDataDTO> getChartData() {
        return stocksService.getChartData();
    }

}
