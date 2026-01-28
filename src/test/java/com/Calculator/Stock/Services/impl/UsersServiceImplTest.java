package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.UserDtoMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Util.JwtUtil;
import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.UserDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserDtoMapper userDtoMapper;

    @InjectMocks
    private UsersServiceImpl usersService;


    @Test
    void loginUser() {
        User user = new User();
        user.setEmail("email");
        user.setPassword("encodedPassword");


        LoginRequest credentials = new LoginRequest();
        credentials.setEmail("email");
        credentials.setPassword("password");

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        when(passwordEncoder.matches(credentials.getPassword(),user.getPassword())).thenReturn(true);

        String token = "Test_token";
        when(jwtUtil.generateToken(credentials.getEmail())).thenReturn(token);


        String response = usersService.LoginUser(credentials);

        assertThat(response).isNotNull();
        assertThat(response).isEqualTo(token);
    }


    @Test
    void registerUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@test.com");
        userDTO.setPassword("rawPassword");

        User newUser = new User();
        newUser.setEmail("test@test.com");
        newUser.setPassword("rawPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setEmail("test@test.com");
        savedUser.setPassword("encodedPassword");

        UserDTO responseDTO = new UserDTO();
        responseDTO.setEmail("test@test.com");
        responseDTO.setPassword("encodedPassword");

        when(userRepository.findByEmail(newUser.getEmail())).thenReturn(Optional.empty());

        when(userDtoMapper.convertUserDTOToUser(userDTO)).thenReturn(newUser);

        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        when(userDtoMapper.convertUserToUserDTO(any(User.class))).thenReturn(responseDTO);

        UserDTO response = usersService.RegisterUser(userDTO);

        assertThat(response).isNotNull();
        assertThat(response.getPassword()).isEqualTo(savedUser.getPassword());

    }

    @Test
    void getUser(){
        User user = new User();
        user.setEmail("test@test.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(userDtoMapper.convertUserToUserDTO(user)).thenReturn(userDTO);

        UserDTO response = usersService.GetUser("test@test.com");

        assertThat(response).isNotNull();
    }

}