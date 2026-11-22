package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.AuditLogDTOMapper;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.AuditLogRespository;
import com.Calculator.Stock.Repository.PortofolioRepository;
import com.Calculator.Stock.Repository.StockRepository;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.EmailSenderService;
import com.Calculator.Stock.Services.PortofolioService;
import com.Calculator.Stock.dto.*;
import com.Calculator.Stock.exception.InsufficientFundsException;
import com.Calculator.Stock.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class PortofolioServiceImpl implements PortofolioService {

    private static final String ACTION_BUY = "BUY";
    private static final String ACTION_SELL = "SELL";
    private static final float SHARES_EPSILON = 0.0001f;

    private final UserRepository userRepository;
    private final PortofolioRepository portofolioRepository;
    private final PortofolioDTOMapper portofolioDTOMapper;
    private final StockRepository stockRepository;
    private final AuditLogRespository auditLogRespository;
    private final AuditLogDTOMapper auditLogDTOMapper;
    private final EmailSenderService emailSenderService;


    private final List<String> targetSymbols = Arrays.asList(
            "AAPL", "TSLA", "GOOGL", "MSFT", "AMZN", "NVDA", "META", "NFLX"
    );


    @Override
    public PortofolioDTO addToPortfolio(BuyStockDTO buyStockDTO) {

        String date = new Date().toString();

        User user = userRepository.findById(buyStockDTO.getUser_id())
                .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));

        if (buyStockDTO.getCurrentPrice() <= 0) {
            throw new InsufficientFundsException("Current price must be greater than 0");
        }
        if (buyStockDTO.getAmountToInvest() <= 0) {
            throw new InsufficientFundsException("The amount to invest must be greater than 0");
        }

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            throw new ResourceNotFoundException("Wallet not found");
        }
        if (buyStockDTO.getAmountToInvest() > wallet.getBalance()) {
            throw new InsufficientFundsException("Insufficient Funds");
        }

        Stock stock = stockRepository.findTopBySymbolOrderByDateDesc(buyStockDTO.getSymbol());
        float latestPrice = stock != null ? stock.getPrice() : buyStockDTO.getCurrentPrice();

        float shares = buyStockDTO.getAmountToInvest() / buyStockDTO.getCurrentPrice();

        // The purchase is paid from the wallet regardless of whether the position already exists.
        wallet.setBalance(wallet.getBalance() - buyStockDTO.getAmountToInvest());

        Optional<Portofolio> portofolioAlrExt = portofolioRepository.findByUserAndSymbol(user, buyStockDTO.getSymbol());
        Portofolio portofolio;

        if (portofolioAlrExt.isPresent()) {
            portofolio = portofolioAlrExt.get();

            float newAmountOwned = portofolio.getAmountOwned() + buyStockDTO.getAmountToInvest();
            float totalShares = portofolio.getShares() + shares;

            if (totalShares > 0) {
                portofolio.setAveragePrice(newAmountOwned / totalShares);
            } else {
                portofolio.setAveragePrice(0);
            }

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

        portofolio.setProfit((latestPrice - portofolio.getAveragePrice()) * portofolio.getShares());

        saveAuditLog(user, ACTION_BUY, buyStockDTO.getSymbol(), buyStockDTO.getCurrentPrice(), shares, buyStockDTO.getAmountToInvest(), date);

        // Persist the wallet change explicitly (Wallet is cascaded from User).
        userRepository.save(user);
        Portofolio savedPortofolio = portofolioRepository.save(portofolio);

        emailSenderService.sendInfoEmail(user.getEmail(), "Thank you for your purchase", "The stock " + buyStockDTO.getSymbol() + " has been purchased successfully" + "\n\n" + "The amount (" + buyStockDTO.getAmountToInvest() + ") has been deducted from your balance. Have a nice day and thank you for your purchase.");

        return portofolioDTOMapper.portofolioToPortofolioDTO(savedPortofolio);
    }


    @Override
    public PortofolioDTO SellToPortfolio(SellStockDTO sellStockDTO) {

        String date = new Date().toString();

        User user = userRepository.findById(sellStockDTO.getUser_id()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (sellStockDTO.getCurrentPrice() <= 0) {
            throw new InsufficientFundsException("Current price must be greater than 0");
        }
        if (sellStockDTO.getWithdrawAmount() <= 0) {
            throw new InsufficientFundsException("The amount to withdraw must be greater than 0");
        }

        Wallet wallet = user.getWallet();
        if (wallet == null) {
            throw new ResourceNotFoundException("Wallet not found");
        }

        Portofolio portofolio = portofolioRepository.findByUserAndSymbol(user, sellStockDTO.getSymbol())
                .orElseThrow(() -> new ResourceNotFoundException("You don't own any shares of " + sellStockDTO.getSymbol()));

        Stock stock = stockRepository.findTopBySymbolOrderByDateDesc(sellStockDTO.getSymbol());
        float latestPrice = stock != null ? stock.getPrice() : sellStockDTO.getCurrentPrice();

        float sharesToSell = sellStockDTO.getWithdrawAmount() / sellStockDTO.getCurrentPrice();
        float ownedShares = portofolio.getShares();

        if (sharesToSell > ownedShares + SHARES_EPSILON) {
            throw new InsufficientFundsException("Sorry you don t have enough shares");
        }

        float newTotalShares = ownedShares - sharesToSell;
        if (newTotalShares < SHARES_EPSILON) {
            newTotalShares = 0;
        }

        // The cost basis (amountOwned) shrinks proportionally to the shares that were sold,
        // so a profitable position can be fully closed.
        float newAmountOwned = 0;
        if (newTotalShares > 0 && ownedShares > 0) {
            newAmountOwned = portofolio.getAmountOwned() * (newTotalShares / ownedShares);
        }

        // The proceeds of the sale go back into the wallet.
        wallet.setBalance(wallet.getBalance() + sellStockDTO.getWithdrawAmount());

        portofolio.setShares(newTotalShares);
        portofolio.setAmountOwned(newAmountOwned);
        portofolio.setProfit((latestPrice - portofolio.getAveragePrice()) * newTotalShares);

        saveAuditLog(user, ACTION_SELL, sellStockDTO.getSymbol(), sellStockDTO.getCurrentPrice(), sharesToSell, sellStockDTO.getWithdrawAmount(), date);

        if (newTotalShares == 0) {
            // Position fully closed: remove the row instead of keeping zeros around.
            if (user.getPortofolio() != null) {
                user.getPortofolio().remove(portofolio);
            }
            portofolioRepository.delete(portofolio);
            userRepository.save(user);

            return portofolioDTOMapper.portofolioToPortofolioDTO(portofolio);
        }

        userRepository.save(user);
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

        // Ignore closed positions (defensive: rows left with zeros by older versions).
        List<Portofolio> openPositions = new ArrayList<>();
        for (Portofolio p : portofolio) {
            if (p.getShares() > 0 || p.getAmountOwned() > 0) {
                openPositions.add(p);
            }
        }

        float total = 0;
        for (Portofolio p : openPositions) {
            total += p.getAmountOwned();
        }

        float marketValue = 0;
        for (Portofolio p : openPositions) {
            PortfolioChartDTO portfolioChartDTO = new PortfolioChartDTO();
            float percentage = 0;

            Stock stock = stockRepository.findTopBySymbolOrderByDateDesc(p.getSymbol());
            float positionMarketValue = stock != null ? p.getShares() * stock.getPrice() : p.getAmountOwned();
            marketValue += positionMarketValue;

            portfolioChartDTO.setSymbol(p.getSymbol());
            portfolioChartDTO.setValue(p.getAmountOwned());

            if (total > 0) {
                percentage = (p.getAmountOwned() / total) * 100;
            }
            portfolioChartDTO.setPercentage(percentage);
            portfolioChartDTO.setMarketValue(positionMarketValue);
            portfolioChartDTO.setProfit(positionMarketValue - p.getAmountOwned());

            portfolioChartDTOList.add(portfolioChartDTO);
        }

        totalDTO.setPortfolioChart(portfolioChartDTOList);
        totalDTO.setUser_Id(user.getId());
        totalDTO.setTotal(total);
        totalDTO.setInvestedValue(total);
        totalDTO.setMarketValue(marketValue);
        totalDTO.setTotalProfit(marketValue - total);
        totalDTO.setBalance(user.getWallet() != null ? user.getWallet().getBalance() : 0);

        return totalDTO;
    }

    @Override
    public DailyProfitDTO getDailyProfit(String symbol) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Optional<Portofolio> position = portofolioRepository.findByUserAndSymbol(user, symbol);
        List<Portofolio> portofolio = portofolioRepository.findAllByUser(user);

        Stock stock = stockRepository.findTopBySymbolOrderByDateDesc(symbol);

        float dailyProfit = 0;
        float dailyProfitPercentage = 0;
        float totalDailyProfit = 0;
        float totalDailyProfitPercentage = 0;
        float totalValAtOpening = 0;

        // Per-symbol fields stay 0 when the user holds no shares of the symbol or no quote exists.
        if (position.isPresent() && position.get().getShares() > 0 && stock != null) {
            dailyProfit = (stock.getPrice() - stock.getOpen()) * position.get().getShares();
            if (stock.getOpen() != 0) {
                dailyProfitPercentage = ((stock.getPrice() - stock.getOpen()) / stock.getOpen()) * 100;
            }
        }

        for (Portofolio p : portofolio) {
            if (p.getShares() <= 0) {
                continue;
            }
            Stock stock1 = stockRepository.findTopBySymbolOrderByDateDesc(p.getSymbol());
            if (stock1 == null) {
                continue;
            }
            totalDailyProfit += (stock1.getPrice() - stock1.getOpen()) * p.getShares();
            totalValAtOpening += stock1.getOpen() * p.getShares();
        }

        if (totalValAtOpening != 0) {
            totalDailyProfitPercentage = (totalDailyProfit / totalValAtOpening) * 100;
        }

        return new DailyProfitDTO(user.getId(), dailyProfit, dailyProfitPercentage, totalDailyProfit, totalDailyProfitPercentage);
    }

    private void saveAuditLog(User user, String action, String symbol, float price, float shares, float total, String date) {
        AuditLogDTO auditLogDTO = new AuditLogDTO();
        auditLogDTO.setUser_id(user.getId());
        auditLogDTO.setDate(date);
        auditLogDTO.setShares(shares);
        auditLogDTO.setSymbol(symbol);
        auditLogDTO.setPrice(price);
        auditLogDTO.setTotal(total);
        auditLogDTO.setAction(action);

        auditLogRespository.save(auditLogDTOMapper.auditLogDTOToAuditLog(auditLogDTO, user));
    }


}
