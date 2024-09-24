package com.kafka.connect.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.AuditLogEntity;
import com.kafka.connect.repository.AuditLogRepository;
import com.kafka.connect.services.AuditLogService;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController
{

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping
    public Page<AuditLogEntity> getAuditLogs(@RequestParam(required = false) String entityName, @RequestParam(required = false) String newValue,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate, Pageable pageable)
    {

        return auditLogService.findAuditLogs(entityName, newValue, startDate, endDate, pageable);
    }

    @GetMapping("/{entityName}")
    public Page<AuditLogEntity> getAuditLogsByEntityName(@PathVariable String entityName, @RequestParam(required = false) String newValue,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate, Pageable pageable)
    {

        return auditLogService.findAuditLogs(entityName, newValue, startDate, endDate, pageable);
    }

    @GetMapping("/{entityName}/{entityId}")
    public List<AuditLogEntity> getAuditLogsEntityNameEntityId(@PathVariable String entityName, @PathVariable Long entityId)
    {
        return auditLogRepository.findByEntityNameAndEntityId(entityName, entityId);
    }
}
