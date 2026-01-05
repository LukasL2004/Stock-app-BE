package com.Calculator.Stock.Repository;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortofolioRepository extends JpaRepository<Portofolio, Long> {
    Optional<Portofolio> findByUserAndSymbol(User user, String symbol);
}
