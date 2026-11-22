package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.AuditLog;
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
import com.Calculator.Stock.dto.*;
import com.Calculator.Stock.exception.InsufficientFundsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.within;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PortofolioServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PortofolioRepository portofolioRepository;

    @Mock
    private PortofolioDTOMapper portofolioDTOMapper;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private AuditLogRespository auditLogRespository;

    @Mock
    private AuditLogDTOMapper auditLogDTOMapper;

    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private PortofolioServiceImpl portofolioService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateAs(String email) {
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(auth.getName()).thenReturn(email);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    private User userWithWallet(Long id, String email, float balance) {
        User user = new User();
        user.setEmail(email);
        user.setId(id);

        Wallet wallet = new Wallet();
        wallet.setId(1);
        wallet.setBalance(balance);
        wallet.setUser(user);
        user.setWallet(wallet);

        return user;
    }

    @Test
    void getPortofolio() {
        String email = "test@test.com";
        String symbol = "AAPL";

        authenticateAs(email);

        User user = new User();
        user.setEmail(email);
        //1L cuz id is type long
        user.setId(1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Portofolio portofolio = new Portofolio();
        portofolio.setSymbol(symbol);
        portofolio.setTotal(50);

        when(portofolioRepository.findByUserAndSymbol(any(), eq(symbol))).thenReturn(Optional.of(portofolio));

        PortofolioDTO portofolioDTO = new PortofolioDTO();
        portofolioDTO.setSymbol(symbol);
        portofolioDTO.setTotal(50);

        when(portofolioDTOMapper.portofolioToPortofolioDTO(portofolio)).thenReturn(portofolioDTO);

        PortofolioDTO rezultat = portofolioService.GetPortofolio(symbol);

        assertThat(rezultat).isNotNull();
        assertThat(rezultat.getSymbol()).isEqualTo(symbol);
        assertThat(rezultat.getTotal()).isEqualTo(50);
    }

    @Test
    void addToPortfolio(){
        String email = "test@test.com";
        String symbol = "AAPL";
        Long userID = 1L;

        User user = userWithWallet(userID, email, 1000f);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Stock stock = new Stock();
        stock.setSymbol(symbol);

        when(stockRepository.findTopBySymbolOrderByDateDesc(symbol)).thenReturn(stock);

        Portofolio portofolio = new Portofolio();
        portofolio.setSymbol(symbol);

        when(portofolioRepository.findByUserAndSymbol(any(), eq(symbol))).thenReturn(Optional.of(portofolio));
        when(portofolioRepository.save(any(Portofolio.class))).thenReturn(portofolio);

        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        when(auditLogRespository.save(any())).thenReturn(auditLog);

        PortofolioDTO portofolioDTO = new PortofolioDTO();
        portofolioDTO.setSymbol(symbol);

        when(portofolioDTOMapper.portofolioToPortofolioDTO(portofolio)).thenReturn(portofolioDTO);


        BuyStockDTO buy  = new BuyStockDTO(userID,symbol,120,500);

        PortofolioDTO response = portofolioService.addToPortfolio(buy);


        assertThat(response).isNotNull();
        assertThat(response.getSymbol()).isEqualTo(symbol);

        // the purchase must be paid from the wallet
        assertThat(user.getWallet().getBalance()).isEqualTo(500f);
        assertThat(portofolio.getShares()).isCloseTo(500f / 120f, within(0.0001f));
        assertThat(portofolio.getAmountOwned()).isEqualTo(500f);
        verify(userRepository).save(user);

        ArgumentCaptor<AuditLogDTO> auditCaptor = ArgumentCaptor.forClass(AuditLogDTO.class);
        verify(auditLogDTOMapper).auditLogDTOToAuditLog(auditCaptor.capture(), eq(user));
        assertThat(auditCaptor.getValue().getAction()).isEqualTo("BUY");
        assertThat(auditCaptor.getValue().getTotal()).isEqualTo(500f);

        verify(emailSenderService).sendInfoEmail(eq(email), anyString(), anyString());
    }

    @Test
    void addToPortfolio_newPositionDeductsWallet(){
        String email = "test@test.com";
        String symbol = "TSLA";
        Long userID = 1L;

        User user = userWithWallet(userID, email, 1000f);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        when(portofolioRepository.findByUserAndSymbol(any(), eq(symbol))).thenReturn(Optional.empty());
        when(portofolioRepository.save(any(Portofolio.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(portofolioDTOMapper.portofolioToPortofolioDTO(any(Portofolio.class))).thenReturn(new PortofolioDTO());

        BuyStockDTO buy = new BuyStockDTO(userID, symbol, 200, 400);

        portofolioService.addToPortfolio(buy);

        assertThat(user.getWallet().getBalance()).isEqualTo(600f);

        ArgumentCaptor<Portofolio> portofolioCaptor = ArgumentCaptor.forClass(Portofolio.class);
        verify(portofolioRepository).save(portofolioCaptor.capture());
        assertThat(portofolioCaptor.getValue().getSymbol()).isEqualTo(symbol);
        assertThat(portofolioCaptor.getValue().getShares()).isEqualTo(2f);
        assertThat(portofolioCaptor.getValue().getAmountOwned()).isEqualTo(400f);
        assertThat(portofolioCaptor.getValue().getAveragePrice()).isEqualTo(200f);
    }

    @Test
    void addToPortfolio_insufficientFunds(){
        Long userID = 1L;
        User user = userWithWallet(userID, "test@test.com", 100f);
        when(userRepository.findById(userID)).thenReturn(Optional.of(user));

        BuyStockDTO buy = new BuyStockDTO(userID, "AAPL", 120, 500);

        assertThatThrownBy(() -> portofolioService.addToPortfolio(buy))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(user.getWallet().getBalance()).isEqualTo(100f);
        verify(portofolioRepository, never()).save(any());
    }

    @Test
    void sellToPortfolio(){
        String email = "test@test.com";
        String symbol = "AAPL";
        Long userID = 1L;

        User user = userWithWallet(userID, email, 100f);

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setPrice(120);
        when(stockRepository.findTopBySymbolOrderByDateDesc(symbol)).thenReturn(stock);

        Portofolio portofolio = new Portofolio();
        portofolio.setSymbol(symbol);
        portofolio.setAmountOwned(550);
        portofolio.setShares(50);
        portofolio.setAveragePrice(11);

        when(portofolioRepository.findByUserAndSymbol(any(), eq(symbol))).thenReturn(Optional.of(portofolio));
        when(portofolioRepository.save(any(Portofolio.class))).thenReturn(portofolio);

        PortofolioDTO portofolioDTO = new PortofolioDTO();
        portofolioDTO.setSymbol(symbol);
        portofolioDTO.setAmountOwned(550);

        when(portofolioDTOMapper.portofolioToPortofolioDTO(portofolio)).thenReturn(portofolioDTO);

        SellStockDTO sell = new SellStockDTO(userID,symbol,120,200);

        PortofolioDTO result = portofolioService.SellToPortfolio(sell);

        assertThat(result).isNotNull();
        assertThat(result.getSymbol()).isEqualTo(symbol);

        // the proceeds of the sale must be credited to the wallet
        assertThat(user.getWallet().getBalance()).isEqualTo(300f);

        float soldShares = 200f / 120f;
        assertThat(portofolio.getShares()).isCloseTo(50f - soldShares, within(0.0001f));
        // cost basis shrinks proportionally to the shares sold
        assertThat(portofolio.getAmountOwned()).isCloseTo(550f * ((50f - soldShares) / 50f), within(0.01f));
        verify(userRepository).save(user);

        ArgumentCaptor<AuditLogDTO> auditCaptor = ArgumentCaptor.forClass(AuditLogDTO.class);
        verify(auditLogDTOMapper).auditLogDTOToAuditLog(auditCaptor.capture(), eq(user));
        assertThat(auditCaptor.getValue().getAction()).isEqualTo("SELL");
        assertThat(auditCaptor.getValue().getTotal()).isEqualTo(200f);
        assertThat(auditCaptor.getValue().getShares()).isCloseTo(soldShares, within(0.0001f));
        verify(auditLogRespository).save(any());
    }

    @Test
    void sellToPortfolio_allSharesRemovesPosition(){
        String symbol = "AAPL";
        Long userID = 1L;

        User user = userWithWallet(userID, "test@test.com", 0f);
        when(userRepository.findById(userID)).thenReturn(Optional.of(user));

        Portofolio portofolio = new Portofolio();
        portofolio.setSymbol(symbol);
        portofolio.setAmountOwned(1000);
        portofolio.setShares(10);
        portofolio.setAveragePrice(100);

        when(portofolioRepository.findByUserAndSymbol(any(), eq(symbol))).thenReturn(Optional.of(portofolio));
        when(portofolioDTOMapper.portofolioToPortofolioDTO(portofolio)).thenReturn(new PortofolioDTO());

        // sell all 10 shares at a price above the average price
        SellStockDTO sell = new SellStockDTO(userID, symbol, 120, 1200);

        PortofolioDTO result = portofolioService.SellToPortfolio(sell);

        assertThat(result).isNotNull();
        assertThat(user.getWallet().getBalance()).isEqualTo(1200f);
        assertThat(portofolio.getShares()).isEqualTo(0f);
        assertThat(portofolio.getAmountOwned()).isEqualTo(0f);
        verify(portofolioRepository).delete(portofolio);
        verify(portofolioRepository, never()).save(any(Portofolio.class));
    }

    @Test
    void sellToPortfolio_notEnoughShares(){
        String symbol = "AAPL";
        Long userID = 1L;

        User user = userWithWallet(userID, "test@test.com", 0f);
        when(userRepository.findById(userID)).thenReturn(Optional.of(user));

        Portofolio portofolio = new Portofolio();
        portofolio.setSymbol(symbol);
        portofolio.setAmountOwned(100);
        portofolio.setShares(1);

        when(portofolioRepository.findByUserAndSymbol(any(), eq(symbol))).thenReturn(Optional.of(portofolio));

        SellStockDTO sell = new SellStockDTO(userID, symbol, 100, 500);

        assertThatThrownBy(() -> portofolioService.SellToPortfolio(sell))
                .isInstanceOf(InsufficientFundsException.class);

        assertThat(user.getWallet().getBalance()).isEqualTo(0f);
        verify(portofolioRepository, never()).save(any());
        verify(portofolioRepository, never()).delete(any());
    }

    @Test
    void getTotal(){
        String email = "test@test.com";
        Long id = 1L;

        authenticateAs(email);

        User user = userWithWallet(id, email, 50f);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Portofolio portofolio = new Portofolio();
        portofolio.setSymbol("AAPL");
        portofolio.setAmountOwned(1000);
        portofolio.setShares(10);

        when(portofolioRepository.findAllByUser(user)).thenReturn(List.of(portofolio));

        Stock stock = new Stock();
        stock.setSymbol("AAPL");
        stock.setPrice(120);
        when(stockRepository.findTopBySymbolOrderByDateDesc("AAPL")).thenReturn(stock);

        TotalDTO totalDTO = new TotalDTO();
        List<PortfolioChartDTO> portfolioChartDTOList = new ArrayList<>();

        totalDTO.setPortfolioChart(portfolioChartDTOList);
        totalDTO.setUser_Id(id);
        totalDTO.setTotal(120);

        TotalDTO result = portofolioService.getTotal();

        assertThat(result).isNotNull();
        assertThat(result.getTotal()).isEqualTo(1000f);
        assertThat(result.getInvestedValue()).isEqualTo(1000f);
        assertThat(result.getMarketValue()).isEqualTo(1200f);
        assertThat(result.getTotalProfit()).isEqualTo(200f);
        assertThat(result.getBalance()).isEqualTo(50f);
        assertThat(result.getPortfolioChart().size()).isEqualTo(1);

        PortfolioChartDTO chart = result.getPortfolioChart().get(0);
        assertThat(chart.getSymbol()).isEqualTo("AAPL");
        assertThat(chart.getValue()).isEqualTo(1000f);
        assertThat(chart.getPercentage()).isEqualTo(100f);
        assertThat(chart.getMarketValue()).isEqualTo(1200f);
        assertThat(chart.getProfit()).isEqualTo(200f);
    }

    @Test
    void getDailyProfit_noPositionReturnsZeros(){
        String email = "test@test.com";
        Long id = 1L;

        authenticateAs(email);

        User user = userWithWallet(id, email, 50f);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(portofolioRepository.findByUserAndSymbol(user, "AAPL")).thenReturn(Optional.empty());
        when(portofolioRepository.findAllByUser(user)).thenReturn(List.of());

        DailyProfitDTO result = portofolioService.getDailyProfit("AAPL");

        assertThat(result).isNotNull();
        assertThat(result.getUser_id()).isEqualTo(id);
        assertThat(result.getDailyProfitValue()).isEqualTo(0f);
        assertThat(result.getDailyProfitPercentage()).isEqualTo(0f);
        assertThat(result.getTotalPortfolioProfitDaily()).isEqualTo(0f);
        assertThat(result.getTotalPortfolioProfitDailyPercentage()).isEqualTo(0f);
    }

    @Test
    void getDailyProfit_withPosition(){
        String email = "test@test.com";
        Long id = 1L;

        authenticateAs(email);

        User user = userWithWallet(id, email, 50f);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        Portofolio portofolio = new Portofolio();
        portofolio.setSymbol("AAPL");
        portofolio.setShares(10);
        portofolio.setAmountOwned(1000);

        when(portofolioRepository.findByUserAndSymbol(user, "AAPL")).thenReturn(Optional.of(portofolio));
        when(portofolioRepository.findAllByUser(user)).thenReturn(List.of(portofolio));

        Stock stock = new Stock();
        stock.setSymbol("AAPL");
        stock.setOpen(100);
        stock.setPrice(110);
        when(stockRepository.findTopBySymbolOrderByDateDesc("AAPL")).thenReturn(stock);

        DailyProfitDTO result = portofolioService.getDailyProfit("AAPL");

        assertThat(result.getDailyProfitValue()).isEqualTo(100f);
        assertThat(result.getDailyProfitPercentage()).isEqualTo(10f);
        assertThat(result.getTotalPortfolioProfitDaily()).isEqualTo(100f);
        assertThat(result.getTotalPortfolioProfitDailyPercentage()).isEqualTo(10f);
    }

}
