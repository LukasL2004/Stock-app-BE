package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.PortofolioRepository;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.BuyStockDTO;
import com.Calculator.Stock.dto.PortofolioDTO;
import com.Calculator.Stock.dto.SellStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortofolioServiceImpl implements PortofolioService {

    private final UserRepository userRepository;
    private final PortofolioRepository portofolioRepository;
    private final PortofolioDTOMapper portofolioDTOMapper;


    @Override
    public PortofolioDTO addToPortfolio(BuyStockDTO buyStockDTO) {

        User user = userRepository.findById(buyStockDTO.getUser_id()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Optional<Portofolio> portofolioAlrExt = portofolioRepository.findByUserAndSymbol(user, buyStockDTO.getSymbol());
        Portofolio portofolio;

        float shares = buyStockDTO.getAmountToInvest() / buyStockDTO.getCurrentPrice();

        if(portofolioAlrExt.isPresent()) {
            portofolio = portofolioAlrExt.get();
            float newAmountOwned =  portofolio.getAmountOwned() + buyStockDTO.getAmountToInvest();
            float toatalShares = portofolio.getShares() + shares;

            float newAverageValue = newAmountOwned / toatalShares;

            portofolio.setAmountOwned(newAmountOwned);
            portofolio.setShares(toatalShares);
            portofolio.setAveragePrice(newAverageValue);

        }else{
            portofolio = new Portofolio();
            portofolio.setUser(user);
            portofolio.setSymbol(buyStockDTO.getSymbol());
            portofolio.setAveragePrice(buyStockDTO.getCurrentPrice());
            portofolio.setShares(shares);
            portofolio.setAmountOwned(buyStockDTO.getAmountToInvest());

        }
           Portofolio Savedportofolio = portofolioRepository.save(portofolio);

        return portofolioDTOMapper.portofolioToPortofolioDTO(Savedportofolio);
    }


    @Override
    public PortofolioDTO SellToPortfolio(SellStockDTO sellStockDTO) {
        User user = userRepository.findById(sellStockDTO.getUser_id()).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Portofolio> existentPortofolio = portofolioRepository.findByUserAndSymbol(user, sellStockDTO.getSymbol());
        Portofolio portofolio;

        if(existentPortofolio.isPresent()) {

            portofolio = existentPortofolio.get();

            float Shares = sellStockDTO.getWithdrawAmount() / sellStockDTO.getCurrentPrice();

            float NewAmountOwned = portofolio.getAmountOwned() - sellStockDTO.getWithdrawAmount();
            float NewTotalShares = portofolio.getShares() - Shares;

            if(portofolio.getShares() >= Shares){
                portofolio.setShares(NewTotalShares);
            }else {
                throw  new RuntimeException("Sorry you don t haev enought shares");
            }
            if(portofolio.getAmountOwned() >= sellStockDTO.getWithdrawAmount()){
                portofolio.setAmountOwned(NewAmountOwned);
            }else {
               throw new RuntimeException("Sorry you don t haev enought");
            }
        }else {
            throw new RuntimeException("User not found");
        }
        Portofolio savePortofolio = portofolioRepository.save(portofolio);

        return portofolioDTOMapper.portofolioToPortofolioDTO(savePortofolio);
    }
}
