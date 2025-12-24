package com.Calculator.Stock.Services.impl;
import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.Mapper.TwelveDataDTOMapper;
import com.Calculator.Stock.Repository.StockRepository;
import com.Calculator.Stock.Services.StocksService;
import com.Calculator.Stock.dto.TwelveDataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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
        for(TwelveDataDTO.Values value :response.getValues()){

            Stock stockOutput = TwelveDataDTOMapper.mapValueToStock(value, currentSymbol);
            stockRepository.save(stockOutput);
        }

            return response;
    }

    @Scheduled(fixedRate =90000 )
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
