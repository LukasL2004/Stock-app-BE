package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Mapper.UserDtoMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.EmailSenderService;
import com.Calculator.Stock.Services.UsersService;
import com.Calculator.Stock.Util.JwtUtil;
import com.Calculator.Stock.dto.ForgotPasswordDTO;
import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.ResetPasswordDTO;
import com.Calculator.Stock.dto.UserDTO;

import com.Calculator.Stock.exception.ResourceNotFoundException;
import com.Calculator.Stock.exception.WrongUserCredentials;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

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
    public void ForgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        User user = userRepository.findByEmail(forgotPasswordDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));

        int code = new Random().nextInt(999999);
        String codeFormatted = String.format("%06d", code);
        user.setResetPasswordToken(codeFormatted);
        user.setResetPasswordTokenTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);
        emailSenderService.sendInfoEmail(forgotPasswordDTO.getEmail(),"Reset Password Code", "Your reset code is:"+ codeFormatted);

    }

    @Override
    public UserDTO ResetPassword(ResetPasswordDTO resetPasswordDTO) {
        User user = userRepository.findByEmail(resetPasswordDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if(user.getResetPasswordToken() == null){
            throw new ResourceNotFoundException("No reset request found. Please request a new code.");
        }
        if(user.getResetPasswordTokenTime().isBefore(LocalDateTime.now())){
            throw new ResourceNotFoundException("Reset password token expired");
        }
        if(!user.getResetPasswordToken().equals(resetPasswordDTO.getCode())){
            throw new ResourceNotFoundException("Reset password token does not match");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenTime(null);
        userRepository.save(user);

        emailSenderService.sendInfoEmail(resetPasswordDTO.getEmail(),"Security Alert: Password Changed", "Your password has been successfully updated.\n\n" +
                "If you did not perform this action, please contact our support team immediately as your account might be compromised.\n\n");
    return userDtoMapper.convertUserToUserDTO(user);
    }
}
