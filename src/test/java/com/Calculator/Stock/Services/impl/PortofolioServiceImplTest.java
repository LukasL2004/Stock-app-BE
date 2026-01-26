package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.AuditLog;
import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.Stock;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.AuditLogDTOMapper;
import com.Calculator.Stock.Mapper.PortofolioDTOMapper;
import com.Calculator.Stock.Repository.AuditLogRespository;
import com.Calculator.Stock.Repository.PortofolioRepository;
import com.Calculator.Stock.Repository.StockRepository;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.dto.BuyStockDTO;
import com.Calculator.Stock.dto.PortofolioDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

    @InjectMocks
    private PortofolioServiceImpl portofolioService;

    @Test
    void getPortofolio() {
        String email = "test@test.com";
        String symbol = "AAPL";

        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);

        when(auth.getName()).thenReturn(email);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

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

        SecurityContextHolder.clearContext();

    }

    @Test
    void addToPortfolio(){
        String email = "test@test.com";
        String symbol = "AAPL";
        Long userID = 1L;

        User user =  new User();
        user.setEmail(email);
        user.setId(userID);
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

        SecurityContextHolder.clearContext();



    }
}