package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Services.AuditLogService;
import com.Calculator.Stock.dto.AuditLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping("/add")
    public AuditLogDTO addToAuditLog(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AuditLogDTO auditLogDTO) {
        return auditLogService.addToAuditLog(auditLogDTO);
    }

    @GetMapping("/get")
    public List<AuditLogDTO> getFromAuditLog( @AuthenticationPrincipal UserDetails userDetails) {
        return auditLogService.GetFromAuditLog();
    }
}
