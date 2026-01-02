package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.PortofolioRepository;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.PortofolioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortofolioServiceImpl implements PortofolioService {

    private final UserRepository userRepository;
    private final PortofolioRepository portofolioRepository;
    PortofolioDTOMapper portofolioDTOMapper;


    @Override
    public PortofolioDTO addToPortfolio(PortofolioDTO dto) {

        User user = userRepository.findById(dto.getUser().getId()).orElseThrow(() -> new RuntimeException("User not found"));
        Portofolio portofolio = portofolioDTOMapper.PortofolioDTOToPortofolio(dto);
        portofolio.setUser(user);

        Portofolio savedPortofolio = portofolioRepository.save(portofolio);

        return dto ;
    }
}
