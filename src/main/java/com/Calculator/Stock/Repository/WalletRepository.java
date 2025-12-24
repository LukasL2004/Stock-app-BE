package com.Calculator.Stock.Repository;

import com.Calculator.Stock.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
}