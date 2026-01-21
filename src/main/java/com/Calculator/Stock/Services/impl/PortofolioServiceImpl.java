package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.AuditLogDTOMapper;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.AuditLogRespository;
import com.Calculator.Stock.Repository.PortofolioRepository;
import com.Calculator.Stock.Repository.StockRepository;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.*;
import com.Calculator.Stock.exception.InsufficientFundsException;
import com.Calculator.Stock.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PortofolioServiceImpl implements PortofolioService {

    private final UserRepository userRepository;
    private final PortofolioRepository portofolioRepository;
    private final PortofolioDTOMapper portofolioDTOMapper;
    private final StockRepository stockRepository;
    private final AuditLogRespository auditLogRespository;
    private final AuditLogDTOMapper auditLogDTOMapper;




    @Override
    public PortofolioDTO addToPortfolio(BuyStockDTO buyStockDTO) {

    String date = new Date().toString();

        User user = userRepository.findById(buyStockDTO.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        Stock stock = stockRepository.findTopBySymbolOrderByDateDesc(buyStockDTO.getSymbol());

        Optional<Portofolio> portofolioAlrExt = portofolioRepository.findByUserAndSymbol(user, buyStockDTO.getSymbol());
        Portofolio portofolio;
        if (buyStockDTO.getCurrentPrice() <= 0) {
            throw new InsufficientFundsException("Current price must be greater than 0");
        }

        float shares = buyStockDTO.getAmountToInvest() / buyStockDTO.getCurrentPrice();

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

            portofolio.setProfit(profit);
            portofolio.setAmountOwned(newAmountOwned);
            portofolio.setShares(totalShares);

            AuditLogDTO auditLogDTO = new AuditLogDTO();
            auditLogDTO.setUser_id(user.getId());
            auditLogDTO.setDate(date);
            auditLogDTO.setShares(shares);
            auditLogDTO.setSymbol(buyStockDTO.getSymbol());
            auditLogDTO.setPrice(buyStockDTO.getCurrentPrice());
            auditLogDTO.setTotal(buyStockDTO.getAmountToInvest());

            auditLogRespository.save(auditLogDTOMapper.auditLogDTOToAuditLog(auditLogDTO,user));


        } else {
            portofolio = new Portofolio();
            portofolio.setUser(user);
            portofolio.setSymbol(buyStockDTO.getSymbol());
            portofolio.setAveragePrice(buyStockDTO.getCurrentPrice());
            portofolio.setShares(shares);
            portofolio.setAmountOwned(buyStockDTO.getAmountToInvest());

            AuditLogDTO auditLogDTO = new AuditLogDTO();
            auditLogDTO.setUser_id(user.getId());
            auditLogDTO.setDate(date);
            auditLogDTO.setShares(shares);
            auditLogDTO.setSymbol(buyStockDTO.getSymbol());
            auditLogDTO.setPrice(buyStockDTO.getCurrentPrice());
            auditLogDTO.setTotal(buyStockDTO.getAmountToInvest());

            auditLogRespository.save(auditLogDTOMapper.auditLogDTOToAuditLog(auditLogDTO,user));


        }

        Portofolio savedPortofolio = portofolioRepository.save(portofolio);

        return portofolioDTOMapper.portofolioToPortofolioDTO(savedPortofolio);
    }


    @Override
    public PortofolioDTO SellToPortfolio(SellStockDTO sellStockDTO) {
        User user = userRepository.findById(sellStockDTO.getUser_id()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
                throw  new InsufficientFundsException("Sorry you don t have enough shares");
            }
            if(portofolio.getAmountOwned() >= sellStockDTO.getWithdrawAmount()){
                portofolio.setAmountOwned(NewAmountOwned);
            }else {
               throw new InsufficientFundsException("Sorry you don t have enough");
            }
        }else {
            throw new ResourceNotFoundException("User not found");
        }
        Portofolio savePortofolio = portofolioRepository.save(portofolio);

        return portofolioDTOMapper.portofolioToPortofolioDTO(savePortofolio);
    }

    @Override
    public PortofolioDTO GetPortofolio(String symbol) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Portofolio> portofolio = portofolioRepository.findByUserAndSymbol(user,symbol);

        if(portofolio.isEmpty()) {
            PortofolioDTO emptyDTO = new PortofolioDTO();
            emptyDTO.setSymbol(symbol);
            emptyDTO.setAmountOwned(0);
            return emptyDTO;
        }

        return portofolioDTOMapper.portofolioToPortofolioDTO(portofolio.get());
    }

    @Override
    public TotalDTO getTotal() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Portofolio> portofolio = portofolioRepository.findAllByUser(user);
        TotalDTO totalDTO = new TotalDTO();
        List<PortfolioChartDTO> portfolioChartDTOList = new ArrayList<>();
        float total = 0;
        for(Portofolio p: portofolio){
            total += p.getAmountOwned();
        }
        for(Portofolio p: portofolio){
        PortfolioChartDTO portfolioChartDTO = new PortfolioChartDTO();
        float percentage = 0;
        portfolioChartDTO.setSymbol(p.getSymbol());
        portfolioChartDTO.setValue(p.getAmountOwned());

        if(total>0){
            percentage = (p.getAmountOwned()/total)*100;
        }
        portfolioChartDTO.setPercentage(percentage);

        portfolioChartDTOList.add(portfolioChartDTO);
        }
        totalDTO.setPortfolioChart(portfolioChartDTOList);
        totalDTO.setUser_Id(user.getId());
        totalDTO.setTotal(total);



        return totalDTO;
    }


}
