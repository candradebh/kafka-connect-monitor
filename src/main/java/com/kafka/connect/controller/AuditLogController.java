package com.kafka.connect.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.AuditLogEntity;
import com.kafka.connect.repository.AuditLogRepository;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController
{

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping("/{entityName}/{entityId}")
    public List<AuditLogEntity> getAuditLogs(@PathVariable String entityName, @PathVariable Long entityId)
    {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId);
    }
}
