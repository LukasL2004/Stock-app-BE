package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Services.EmailSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender mailSender;

    @Async
    @Override
    public void sendInfoEmail(String to, String subject, String body) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("laza.lukas04@gmail.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to " + to);
        }catch (Exception e){
        log.error(e.getMessage());
        throw new RuntimeException(e);
        }

    }
}
