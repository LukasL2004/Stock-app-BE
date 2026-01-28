package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.WalletDTOMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Repository.WalletRepository;
import com.Calculator.Stock.dto.WalletDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    WalletDTOMapper walletDTOMapper;

    @InjectMocks
    WalletServiceImpl walletServiceImpl;


    @Test
    void AddFounds(){
        int id = 1;
        Wallet wallet = new Wallet();
        wallet.setId(id);
        float amount = 10.0f;


        when(walletRepository.findById((long)wallet.getId())).thenReturn(Optional.of(wallet));

        WalletDTO dto = new WalletDTO();
        dto.setId(id);

        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(walletDTOMapper.WalletDTOMapper(wallet)).thenReturn(dto);

        WalletDTO response = walletServiceImpl.AddFounds(dto,amount);

        assertThat(response).isNotNull();

    }

    @Test
    void Withdraw(){
        int id = 1;
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setBalance(100.0f);
        float amount = 10.0f;

        when(walletRepository.findById((long)wallet.getId())).thenReturn(Optional.of(wallet));

        WalletDTO dto = new WalletDTO();
        dto.setId(id);

        when(walletRepository.save(wallet)).thenReturn(wallet);
        when(walletDTOMapper.WalletDTOMapper(wallet)).thenReturn(dto);

        WalletDTO response =  walletServiceImpl.Withdraw(dto,amount);

        assertThat(response).isNotNull();
    }

    @Test
    void FindWallet(){
        String email = "test@test.com";
        int id = 1;
        User user = new User();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        Wallet wallet = user.getWallet();
        WalletDTO dto = new WalletDTO();

        when(walletDTOMapper.WalletDTOMapper(wallet)).thenReturn(dto);

        WalletDTO response = walletServiceImpl.GetBalance(email);

        assertThat(response).isNotNull();
    }
}