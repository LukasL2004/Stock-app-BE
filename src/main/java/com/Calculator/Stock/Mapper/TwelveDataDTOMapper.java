package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.dto.ChartDataDTO;
import com.Calculator.Stock.dto.StocksDTO;
import com.Calculator.Stock.dto.TwelveDataDTO;

public class TwelveDataDTOMapper {
    public static Stock mapValueToStock(TwelveDataDTO.Values valuesDto,String status, String symbol) {
        if (valuesDto == null) {
            return null;
        }

        Stock stock = new Stock();


        stock.setSymbol(symbol);


        stock.setDate(valuesDto.getDatetime());
        stock.setPrice(Float.parseFloat(valuesDto.getClose()));


        stock.setOpen(Float.parseFloat(valuesDto.getOpen()));
        stock.setHigh(Float.parseFloat(valuesDto.getHigh()));
        stock.setLow(Float.parseFloat(valuesDto.getLow()));
        stock.setStatus(status);
        stock.setVolume(valuesDto.getVolume());

        return stock;
    }

    public static StocksDTO StockToDTOMapper(Stock stock) {
        return new StocksDTO(
                stock.getId(),
                stock.getSymbol(),
                stock.getDate(),
                stock.getPrice(),
                stock.getOpen(),
                stock.getHigh(),
                stock.getLow(),
                stock.getVolume(),
                stock.getStatus()
        );


    }


    public static ChartDataDTO StockToChartDataDTOMapper(Stock stock) {
        String fullDateTime = stock.getDate();
        String date = null;
        String time = null;

        if(fullDateTime != null && fullDateTime.contains(" ")) {
            String[] splitDateTime = fullDateTime.split(" ");
            date = splitDateTime[0];
            time = splitDateTime[1];
        }

        return new ChartDataDTO(
                stock.getSymbol(),
                time,
                date,
                stock.getPrice()
        );
    }
}
