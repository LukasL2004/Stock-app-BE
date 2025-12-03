package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.UserService;
import com.Calculator.Stock.dto.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public User registerUser(@RequestBody User user) {

        return userService.registerUser(user);
    }

    @GetMapping("/Email/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.FindUserByEmail(email);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
           try{
               String jwtToken = userService.login(loginRequest.getEmail(), loginRequest.getPassword());
               Map<String, String> map = new HashMap<>();
               map.put("token", jwtToken);

               return ResponseEntity.ok(map);
           }catch (Exception e){
               return ResponseEntity.badRequest().body(e.getMessage());
           }
    }
}
