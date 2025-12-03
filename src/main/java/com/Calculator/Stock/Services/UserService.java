package com.Calculator.Stock.Services;

import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Entity.Wallet;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Repository.WalletRepository;
import com.Calculator.Stock.Util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final WalletRepository walletRepository;


    public UserService(UserRepository userRepository , JwtUtil jwtUtil, PasswordEncoder passwordEncoder, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.walletRepository = walletRepository;
    }

    public User registerUser(User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new RuntimeException("Acest email este deja înregistrat.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Wallet wallet = new Wallet();
        wallet.setBalance(0);
        wallet.setUser(user);
        user.setWallet(wallet);

        return userRepository.save(user);
    }

    public User FindUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Utilizatorul nu a fost găsit."));
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Credentiale incorecte."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credentiale incorecte.");
        }

        String token = jwtUtil.generateToken(email);

        return token;

    }
}
