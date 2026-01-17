package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.dto.UserDTO;

public class UserDtoMapper {

    public static UserDTO convertUserToUserDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getFirstName(),
                user.getLastName(),
                user.getWallet(),
                user.getPortofolio(),
                user.getAuditLogs()
        );
    }

    public static User convertUserDTOToUser(UserDTO userDTO) {
        return new User(
                userDTO.getId(),
                userDTO.getEmail(),
                userDTO.getPassword(),
                userDTO.getFirstName(),
                userDTO.getLastName(),
                userDTO.getWallet(),
                userDTO.getPortofolio(),
                userDTO.getAuditLogs()
        );
    }

}
