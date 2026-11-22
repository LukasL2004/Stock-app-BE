package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.UserDtoMapper;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.EmailSenderService;
import com.Calculator.Stock.Util.JwtUtil;
import com.Calculator.Stock.dto.ForgotPasswordDTO;
import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.UserDTO;
import com.Calculator.Stock.exception.UserAlreadyExistsException;
import com.Calculator.Stock.exception.WrongUserCredentials;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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
    @Mock
    private EmailSenderService emailSenderService;

    @InjectMocks
    private UsersServiceImpl usersService;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }


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
        verify(emailSenderService).sendInfoEmail(eq("test@test.com"), anyString(), anyString());

    }

    @Test
    void registerUser_existingEmailThrowsConflict() {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@test.com");
        userDTO.setPassword("rawPassword");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> usersService.RegisterUser(userDTO))
                .isInstanceOf(UserAlreadyExistsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void loginUser_wrongPassword() {
        User user = new User();
        user.setEmail("email");
        user.setPassword("encodedPassword");

        LoginRequest credentials = new LoginRequest();
        credentials.setEmail("email");
        credentials.setPassword("wrong");

        when(userRepository.findByEmail("email")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThatThrownBy(() -> usersService.LoginUser(credentials))
                .isInstanceOf(WrongUserCredentials.class);
    }

    @Test
    void forgotPassword_storesSixDigitCode() {
        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        usersService.ForgotPassword(new ForgotPasswordDTO("test@test.com"));

        assertThat(user.getResetPasswordToken()).matches("\\d{6}");
        assertThat(user.getResetPasswordTokenTime()).isNotNull();
        verify(userRepository).save(user);
        verify(emailSenderService).sendInfoEmail(eq("test@test.com"), anyString(), anyString());
    }

    @Test
    void getUser(){
        Authentication auth = mock(Authentication.class);
        SecurityContext context = mock(SecurityContext.class);
        when(auth.getName()).thenReturn("test@test.com");
        when(context.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(context);

        User user = new User();
        user.setEmail("test@test.com");

        UserDTO userDTO = new UserDTO();
        userDTO.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(userDtoMapper.convertUserToUserDTO(user)).thenReturn(userDTO);

        UserDTO response = usersService.GetUser();

        assertThat(response).isNotNull();
    }

}