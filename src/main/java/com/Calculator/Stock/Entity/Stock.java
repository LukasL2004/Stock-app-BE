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
@Document(collection = "stocks")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String symbol;
    private String date;
    private float price;
    private float open;
    private float high;
    private float low;
    private String volume;
    private String status;
}
