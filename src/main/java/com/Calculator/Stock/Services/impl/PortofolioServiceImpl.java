package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.PortofolioRepository;
import com.Calculator.Stock.Repository.StockRepository;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.BuyStockDTO;
import com.Calculator.Stock.dto.PortofolioDTO;
import com.Calculator.Stock.dto.SellStockDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortofolioServiceImpl implements PortofolioService {

    private final UserRepository userRepository;
    private final PortofolioRepository portofolioRepository;
    private final PortofolioDTOMapper portofolioDTOMapper;
    private final StockRepository stockRepository ;

    private final List<String> targetSymbols = Arrays.asList(
            "AAPL", "TSLA", "GOOGL", "MSFT", "AMZN", "NVDA", "META", "NFLX"
    );


    @Override
    public PortofolioDTO addToPortfolio(BuyStockDTO buyStockDTO) {

        User user = userRepository.findById(buyStockDTO.getUser_id())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        Stock stock = stockRepository.findTopBySymbolOrderByDateDesc(buyStockDTO.getSymbol());
        float total = 0;

        Optional<Portofolio> portofolioAlrExt = portofolioRepository.findByUserAndSymbol(user, buyStockDTO.getSymbol());
        Portofolio portofolio;
        if (buyStockDTO.getCurrentPrice() <= 0) {
            throw new RuntimeException("Current price must be greater than 0");
        }

        float shares = buyStockDTO.getAmountToInvest() / buyStockDTO.getCurrentPrice();
        for(String target : targetSymbols){
        Portofolio portofolio1 = portofolioRepository.findByUserAndSymbol(user,target).orElseThrow(() -> new RuntimeException("User Not Found"));

        total = total + portofolio1.getAmountOwned();

        }

        if(portofolioAlrExt.isPresent()) {
            portofolio = portofolioAlrExt.get();

            float newAmountOwned = portofolio.getAmountOwned() + buyStockDTO.getAmountToInvest();
            float totalShares = portofolio.getShares() + shares;

            if (totalShares > 0) {
                float newAverageValue = newAmountOwned / totalShares;
                portofolio.setAveragePrice(newAverageValue);
            } else {
                portofolio.setAveragePrice(0);
            }

            float profit = (stock.getPrice() - portofolio.getAveragePrice()) * portofolio.getShares();

            portofolio.setTotal(total);
            portofolio.setProfit(profit);
            portofolio.setAmountOwned(newAmountOwned);
            portofolio.setShares(totalShares);

        } else {
            portofolio = new Portofolio();
            portofolio.setUser(user);
            portofolio.setSymbol(buyStockDTO.getSymbol());
            portofolio.setAveragePrice(buyStockDTO.getCurrentPrice());
            portofolio.setShares(shares);
            portofolio.setAmountOwned(buyStockDTO.getAmountToInvest());
        }

        Portofolio savedPortofolio = portofolioRepository.save(portofolio);

        return portofolioDTOMapper.portofolioToPortofolioDTO(savedPortofolio);
    }


    @Override
    public PortofolioDTO SellToPortfolio(SellStockDTO sellStockDTO) {
        User user = userRepository.findById(sellStockDTO.getUser_id()).orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Portofolio> existentPortofolio = portofolioRepository.findByUserAndSymbol(user, sellStockDTO.getSymbol());
        Stock stock = stockRepository.findTopBySymbolOrderByDateDesc(sellStockDTO.getSymbol());
        Portofolio portofolio;

        if(existentPortofolio.isPresent()) {

            portofolio = existentPortofolio.get();

            float Shares = sellStockDTO.getWithdrawAmount() / sellStockDTO.getCurrentPrice();

            if (Shares < 0.00001) {
                Shares = 0;
            }

            float NewAmountOwned = portofolio.getAmountOwned() - sellStockDTO.getWithdrawAmount();
            float NewTotalShares = portofolio.getShares() - Shares;
            float profit = (stock.getPrice() - portofolio.getAveragePrice()) * portofolio.getShares();

            portofolio.setProfit(profit);


            if(portofolio.getShares() >= Shares){
                if(NewTotalShares < 0.0001) {
                    NewTotalShares = 0;
                }
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

    @Override
    public PortofolioDTO GetPortofolio(String symbol) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Portofolio portofolio = portofolioRepository.findByUserAndSymbol(user,symbol).orElseThrow(() -> new RuntimeException("Portofolio not found"));

        return portofolioDTOMapper.portofolioToPortofolioDTO(portofolio);
    }

    @Override
    public float GetProfit(String symbol) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return 0;
    }

}
