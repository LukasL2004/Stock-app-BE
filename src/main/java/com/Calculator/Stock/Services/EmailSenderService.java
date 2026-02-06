package com.Calculator.Stock.Services;

import org.springframework.stereotype.Service;

@Service
public interface EmailSenderService {
    void sendInfoEmail(String to, String subject, String body);
}
