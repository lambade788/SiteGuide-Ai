package com.siteguard.service;

import com.siteguard.entity.AuditLog;
import com.siteguard.repository.AuditLogRepository;

import java.util.List;

public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService() {
        this.auditLogRepository = new AuditLogRepository();
    }

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLog logAction(String action, String entityType, Long entityId, String description) {
        AuditLog log = new AuditLog(action, entityType, entityId, description);
        return auditLogRepository.save(log);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}
