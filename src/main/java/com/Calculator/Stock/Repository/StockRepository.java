package com.Calculator.Stock.Repository;

import com.Calculator.Stock.Entity.Stock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends MongoRepository<Stock,String> {

    Stock findTopByOrderByIdDesc(String symbol);

    Stock findTopBySymbolOrderByDateDesc(String symbol);

    boolean existsBySymbolAndDate(String symbol, String date);

    @Query(value = "{'symbol': ?0}")
    List<Stock> findAllBySymbol(String symbol);
}
