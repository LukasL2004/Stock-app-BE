package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.dto.PortofolioDTO;

public class PortofolioDTOMapper {

    public Portofolio PortofolioDTOToPortofolio(PortofolioDTO portofolioDTO) {
        return new Portofolio(
                portofolioDTO.getId(),
                portofolioDTO.getSymbol(),
                portofolioDTO.getQuantity(),
                portofolioDTO.getAveragePrice(),
                portofolioDTO.getUser()
        );
    }

        public PortofolioDTO portofolioToPortofolioDTO(Portofolio portofolio){
        return new PortofolioDTO(
                portofolio.getId(),
                portofolio.getSymbol(),
                portofolio.getQuantity(),
                portofolio.getAveragePrice(),
                portofolio.getUser()
        );
    }
}
