package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.dto.TwelveDataDTO;

public class TwelveDataDTOMapper {
    public static Stock mapValueToStock(TwelveDataDTO.Values valuesDto, String symbol) {
        if (valuesDto == null) {
            return null;
        }

        Stock stock = new Stock();


        stock.setSymbol(symbol);


        stock.setDate(valuesDto.getDatetime());
        stock.setPrice(valuesDto.getClose());


        stock.setOpen(valuesDto.getOpen());
        stock.setHigh(valuesDto.getHigh());
        stock.setLow(valuesDto.getLow());
        stock.setVolume(valuesDto.getVolume());

        return stock;
    }
}
