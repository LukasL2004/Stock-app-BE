package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.UserDtoMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.UsersService;
import com.Calculator.Stock.Util.JwtUtil;
import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.UserDTO;

import com.Calculator.Stock.exception.ResourceNotFoundException;
import com.Calculator.Stock.exception.WrongUserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements UsersService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;

    @Override
    public UserDTO RegisterUser(UserDTO userDTO) {
        if(userRepository.findByEmail(userDTO.getEmail()).isPresent()){
            throw new RuntimeException("User already exists");
        }
        User user = UserDtoMapper.convertUserDTOToUser(userDTO);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0);
        wallet.setInvestment(0);
        user.setWallet(wallet);

        userRepository.save(user);


        return UserDtoMapper.convertUserToUserDTO(user);
    }

    @Override
    public String LoginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new WrongUserCredentials("Incorrect password");
        }
        String Token = jwtUtil.generateToken(loginRequest.getEmail());
        return Token;
    }

    @Override
    public UserDTO GetUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return  UserDtoMapper.convertUserToUserDTO(user);
    }
}
