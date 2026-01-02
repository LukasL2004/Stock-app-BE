package com.Calculator.Stock.Services.impl;
import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.Mapper.TwelveDataDTOMapper;
import com.Calculator.Stock.Repository.StockRepository;
import com.Calculator.Stock.Services.StocksService;
import com.Calculator.Stock.dto.ChartDataDTO;
import com.Calculator.Stock.dto.StocksDTO;
import com.Calculator.Stock.dto.TwelveDataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class StockServiceImpl implements StocksService {

    private final List<String> targetSymbols = Arrays.asList(
            "AAPL", "TSLA", "GOOGL", "MSFT", "AMZN", "NVDA", "META", "NFLX"
    );

    private final String apiKey;
    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;

    public StockServiceImpl(@Value("${twelvedata.apikey}") String apiKey, StockRepository stockRepository) {
        this.apiKey = apiKey;
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
    }


    @Override
    public TwelveDataDTO getStocks(String symbol) {

        String API_URL = "https://api.twelvedata.com/time_series?symbol=" + symbol + "&interval=15min&outputsize=1&apikey=" + apiKey;

        TwelveDataDTO response = restTemplate.getForObject(API_URL, TwelveDataDTO.class);

        if(response==null){
            System.out.println("Error");
            return null;
        }

        String currentSymbol = response.getMeta().getSymbol();
        String currentStatus = response.getStatus();
        for(TwelveDataDTO.Values value :response.getValues()){

            Stock stockOutput = TwelveDataDTOMapper.mapValueToStock(value,currentStatus ,currentSymbol);
            boolean exists = stockRepository.existsBySymbolAndDate(currentSymbol, stockOutput.getDate());
            if(!exists){
            stockRepository.save(stockOutput);
            System.out.println("Added stock: " + currentSymbol);
            }else{
                System.out.println("Duplicate stock: " + currentSymbol);
            }
        }

            return response;
    }

    @Override
    public List<StocksDTO> getStocksValues() {
        List<StocksDTO> allStocksList = new ArrayList<>();

        try {
            for (String targetSymbol : targetSymbols) {
                Stock stocksValue = stockRepository.findTopBySymbolOrderByDateDesc(targetSymbol);
                if (stocksValue != null) {
                    StocksDTO stocksDTO = TwelveDataDTOMapper.StockToDTOMapper(stocksValue);
                    allStocksList.add(stocksDTO);
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }


        return allStocksList;
    }

    @Override
    public List<ChartDataDTO> getChartData() {
        List<ChartDataDTO> allChartDataList = new ArrayList<>();
        try {
            for (String targetSymbol : targetSymbols) {
                List<Stock> stocksDataList = stockRepository.findAllBySymbol(targetSymbol);

                if (stocksDataList != null && !stocksDataList.isEmpty()) {
                    for (Stock stockData : stocksDataList) {
                        ChartDataDTO chartDataDTO = TwelveDataDTOMapper.StockToChartDataDTOMapper(stockData);
                        allChartDataList.add(chartDataDTO);
                    }
                }
            }
        } catch (Exception e) {
                throw new RuntimeException(e);
            }

        return allChartDataList;
    }

    @Scheduled(fixedRate =1800000 )
    public void updateStocks() {
        System.out.println("Updating stocks");
        for(String symbol :targetSymbols){
            try{
            getStocks(symbol);
            Thread.sleep(15000);
            }catch (Exception e){
                System.out.println("Error: "+e.getMessage());
            }
        }
        System.out.println("Updated Stocks");
    }

}
