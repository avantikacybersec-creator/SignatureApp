package com.signature.signatureapp.service;

import com.signature.signatureapp.model.AuditLog;
import com.signature.signatureapp.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditService {

    private final AuditLogRepository repository;

    public AuditService(AuditLogRepository repository) {
        this.repository = repository;
    }

    public void log(
            String email,
            String action,
            String documentName) {

        AuditLog audit = new AuditLog();

        audit.setUserEmail(email);
        audit.setAction(action);
        audit.setDocumentName(documentName);
        audit.setCreatedAt(LocalDateTime.now());

        repository.save(audit);
    }

    public List<AuditLog> getAllLogs() {
        return repository.findAll();
    }
}