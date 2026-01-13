package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.dto.PortofolioDTO;
import org.springframework.stereotype.Component;

@Component
public class PortofolioDTOMapper {

    public Portofolio PortofolioDTOToPortofolio(PortofolioDTO portofolioDTO, User user) {
        return new Portofolio(
                portofolioDTO.getId() != null ? portofolioDTO.getId() : 0,
                portofolioDTO.getSymbol(),
                portofolioDTO.getAveragePrice(),
                portofolioDTO.getAmountOwned(),
                portofolioDTO.getShares(),
                portofolioDTO.getProfit(),
                portofolioDTO.getTotal(),
                user
        );
    }

    public PortofolioDTO portofolioToPortofolioDTO(Portofolio portofolio){
        return new PortofolioDTO(
                portofolio.getId(),
                portofolio.getSymbol(),
                portofolio.getAveragePrice(),
                portofolio.getAmountOwned(),
                portofolio.getShares(),
                portofolio.getProfit(),
                portofolio.getTotal(),
                portofolio.getUser() != null ? portofolio.getUser().getId() : null
        );
    }
}