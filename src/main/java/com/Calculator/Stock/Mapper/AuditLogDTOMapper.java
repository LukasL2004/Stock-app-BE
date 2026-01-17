package com.Calculator.Stock.Mapper;

import com.Calculator.Stock.Entity.AuditLog;
import com.Calculator.Stock.Entity.User;
import com.Calculator.Stock.dto.AuditLogDTO;
import org.springframework.stereotype.Component;


@Component
public class AuditLogDTOMapper {
    public AuditLogDTO auditLogToAuditLogDTO(AuditLog auditLog) {
        Long userId = (auditLog.getUser() != null) ? auditLog.getUser().getId() : null;
        return new AuditLogDTO(
                auditLog.getId(),
                auditLog.getSymbol(),
                auditLog.getTotal(),
                auditLog.getDate(),
                auditLog.getPrice(),
                auditLog.getShares(),
                userId
                );
    }public AuditLog auditLogDTOToAuditLog(AuditLogDTO auditLog, User user) {
        return new AuditLog(
                auditLog.getId(),
                auditLog.getSymbol(),
                auditLog.getTotal(),
                auditLog.getDate(),
                auditLog.getPrice(),
                auditLog.getShares(),
                user

                );
    }
}
