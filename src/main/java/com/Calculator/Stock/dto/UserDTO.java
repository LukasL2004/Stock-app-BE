package com.Calculator.Stock.dto;

import com.Calculator.Stock.Entity.AuditLog;
import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.Wallet;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long id;
    @NotBlank(message = "Email is required")
    @Email(message = "Please enter a valid email!")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenTime;
    private Wallet wallet;
    private List<Portofolio> portofolio;
    private List<AuditLog> auditLogs;
}
