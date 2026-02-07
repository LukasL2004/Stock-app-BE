package com.Calculator.Stock.dto;

import com.Calculator.Stock.Entity.AuditLog;
import com.Calculator.Stock.Entity.Portofolio;
import com.Calculator.Stock.Entity.Wallet;
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
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String resetPasswordToken;
    private LocalDateTime resetPasswordTokenTime;
    private Wallet wallet;
    private List<Portofolio> portofolio;
    private List<AuditLog> auditLogs;
}
