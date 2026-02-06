package com.Calculator.Stock.Services;


import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.ResetPasswordDTO;
import com.Calculator.Stock.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
public interface UsersService {
    UserDTO RegisterUser(UserDTO user);
    String LoginUser(LoginRequest loginRequest);
    UserDTO GetUser(String email);
    UserDTO ResetPassword(ResetPasswordDTO resetPasswordDTO);


}
