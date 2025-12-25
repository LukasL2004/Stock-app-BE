package com.Calculator.Stock.Repository;

import com.Calculator.Stock.Entity.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends MongoRepository<Stock,String> {

    Stock findTopByOrderByIdDesc(String symbol);

    Stock findTopBySymbolOrderByDateDesc(String symbol);

    boolean existsBySymbolAndDate(String symbol, String date);
}
