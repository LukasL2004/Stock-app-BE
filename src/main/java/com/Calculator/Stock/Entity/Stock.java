package com.Calculator.Stock.Entity;

import com.Calculator.Stock.dto.TwelveDataDTO;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;



@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
@Document(collation = "Stocks_db")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String symbol;


    private String date;
    private String price;

    private String open;
    private String high;
    private String low;
    private String volume;


}
//}
//Symbol
//:
//        "AAPL:NASDAQ"
//candle
//:
//close
//:
//        "272.3"
//datetime
//:
//        "2025-12-22 11:00:00"
//high
//:
//        "272.41"
//low
//:
//        "272.25"
//        open
//:
//        "272.33"
//        volume
//        :
//        "7027"