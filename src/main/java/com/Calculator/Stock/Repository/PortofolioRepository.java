package com.Calculator.Stock.Repository;

import com.Calculator.Stock.Entity.Portofolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortofolioRepository extends JpaRepository<Portofolio, Long> {
}
