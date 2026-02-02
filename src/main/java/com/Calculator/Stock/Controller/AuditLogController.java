package com.Calculator.Stock.Controller;

import com.Calculator.Stock.Services.AuditLogService;
import com.Calculator.Stock.dto.AuditLogDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/add")
    public AuditLogDTO addToAuditLog(@AuthenticationPrincipal UserDetails userDetails, @RequestBody AuditLogDTO auditLogDTO) {

        messagingTemplate.convertAndSendToUser(userDetails.getUsername(),"/topic/audit", auditLogDTO);

        return auditLogService.addToAuditLog(auditLogDTO);
    }

    @GetMapping("/get")
    public List<AuditLogDTO> getFromAuditLog( @AuthenticationPrincipal UserDetails userDetails) {
        return auditLogService.GetFromAuditLog();
    }
}
