package com.Calculator.Stock.Controller;
import com.Calculator.Stock.Services.UsersService;
import com.Calculator.Stock.dto.LoginRequest;
import com.Calculator.Stock.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UsersService usersService;


    @PostMapping("/register")
    public UserDTO registerUser(@RequestBody UserDTO user) {

        return usersService.RegisterUser(user);
    }

    @GetMapping
    public UserDTO getUserByEmail(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        return usersService.GetUser(email);
    }

    @PostMapping("/login")
   public ResponseEntity<Map<String,String>> LoginUser(@RequestBody LoginRequest loginRequest) {
        String JwtToken = usersService.LoginUser(loginRequest);
        Map<String,String> map = new HashMap<>();
        map.put("token",JwtToken);
        return ResponseEntity.ok(map);
    }
}
