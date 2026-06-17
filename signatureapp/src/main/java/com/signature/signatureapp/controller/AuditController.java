package com.signature.signatureapp.controller;

import com.signature.signatureapp.model.AuditLog;
import com.signature.signatureapp.service.AuditService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping
    public List<AuditLog> getLogs() {
        return auditService.getAllLogs();
    }
}