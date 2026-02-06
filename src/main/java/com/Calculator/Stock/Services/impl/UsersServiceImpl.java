package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.UserDtoMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.EmailSenderService;
import com.Calculator.Stock.Services.UsersService;
import com.Calculator.Stock.Util.JwtUtil;
import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.ResetPasswordDTO;
import com.Calculator.Stock.dto.UserDTO;

import com.Calculator.Stock.exception.ResourceNotFoundException;
import com.Calculator.Stock.exception.WrongUserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements UsersService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDtoMapper userDtoMapper;
    private final JwtUtil jwtUtil;
    private final EmailSenderService emailSenderService;

    @Override
    public UserDTO RegisterUser(UserDTO userDTO) {
        if(userRepository.findByEmail(userDTO.getEmail()).isPresent()){
            throw new RuntimeException("User already exists");
        }
        User user = userDtoMapper.convertUserDTOToUser(userDTO);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(0);
        wallet.setInvestment(0);
        user.setWallet(wallet);

        userRepository.save(user);

        emailSenderService.sendInfoEmail(
                userDTO.getEmail(),
                "Welcome to WealthGrow",
                "Thanks for choosing WealthGrow " + userDTO.getEmail() + "\n\n" +
                        "We are pleased to inform you that our application is made for experienced investors as well as beginners starting their journey."
        );

        return userDtoMapper.convertUserToUserDTO(user);
    }

    @Override
    public String LoginUser(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new WrongUserCredentials("Incorrect password");
        }
          return jwtUtil.generateToken(loginRequest.getEmail());
    }

    @Override
    public UserDTO GetUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return  userDtoMapper.convertUserToUserDTO(user);
    }

    @Override
    public UserDTO ResetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository.findByEmail(resetPasswordDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        emailSenderService.sendInfoEmail(user.getEmail(),"Reset password code", "Your code: "+ code);

        if(code == resetPasswordDTO.getCode()){
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        }
        userRepository.save(user);
        return userDtoMapper.convertUserToUserDTO(user);
    }
}
