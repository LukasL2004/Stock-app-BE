package com.Calculator.Stock.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Portofolio")
public class Portofolio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
   private String symbol;
    @Column(nullable = false)
    private float averagePrice;
    @Column(nullable = false )
    private float amountOwned;
    @Column(nullable = false)
    private float shares;
    @Column(nullable = false)
    private float profit;
    @Column(nullable = false)
    private float total;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
