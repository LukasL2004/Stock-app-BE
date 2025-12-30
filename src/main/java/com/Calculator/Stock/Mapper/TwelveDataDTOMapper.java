package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.Stock;
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

}
