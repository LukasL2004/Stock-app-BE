package com.Calculator.Stock.Services.impl;

import com.Calculator.Stock.Entity.AuditLog;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.Mapper.AuditLogDTOMapper;
import com.Calculator.Stock.Repository.AuditLogRespository;
import com.Calculator.Stock.Repository.UserRepository;
import com.Calculator.Stock.Services.AuditLogService;
import com.Calculator.Stock.dto.AuditLogDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final UserRepository userRepository;
    private final AuditLogRespository auditLogRespository;
    private final AuditLogDTOMapper auditLogDTOMapper ;

    @Override
    @Transactional
    public AuditLogDTO addToAuditLog(AuditLogDTO auditLogDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        auditLogDTO.setUser_id(user.getId());

        AuditLog auditLog = auditLogDTOMapper.auditLogDTOToAuditLog(auditLogDTO,user);

        AuditLog SavedauditLog = auditLogRespository.save(auditLog);

        return auditLogDTOMapper.auditLogToAuditLogDTO(SavedauditLog);
    }

    @Override
    public List<AuditLogDTO> GetFromAuditLog() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        List<AuditLog> auditLog = auditLogRespository.findAllByUser(user);

        return auditLog.stream().map(auditLogDTOMapper::auditLogToAuditLogDTO).collect(Collectors.toList());
    }
}
