package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {

    public UserDTO convertUserToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getResetPasswordToken(),
                user.getResetPasswordTokenTime(),
                user.getWallet(),
                user.getPortofolio(),
                user.getAuditLogs()
        );
    }

    public User convertUserDTOToUser(UserDTO userDTO) {
        return new User(
                userDTO.getId(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getResetPasswordToken(),
                userDTO.getResetPasswordTokenTime(),
                userDTO.getWallet(),
                userDTO.getPortofolio(),
                userDTO.getAuditLogs()
        );
    }

}
