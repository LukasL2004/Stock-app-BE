package com.Calculator.Stock.Services;


import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.ResetPasswordDTO;
import com.Calculator.Stock.dto.UserDTO;
import com.Calculator.Stock.dto.ForgotPasswordDTO;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {
    UserDTO RegisterUser(UserDTO user);
    String LoginUser(LoginRequest loginRequest);
    UserDTO GetUser(String email);
    void ForgotPassword(ForgotPasswordDTO forgotPasswordDTO);
    UserDTO ResetPassword(ResetPasswordDTO resetPasswordDTO);


}
