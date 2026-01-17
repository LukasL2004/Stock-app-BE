package com.Calculator.Stock.Services;

import com.Calculator.Stock.dto.AuditLogDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AuditLogService {
    AuditLogDTO addToAuditLog(AuditLogDTO auditLogDTO);
    List<AuditLogDTO> GetFromAuditLog();
}
