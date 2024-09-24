package com.kafka.connect.services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kafka.connect.entity.AuditLogEntity;
import com.kafka.connect.repository.AuditLogRepository;

@Service
public class AuditLogService
{
    @Value("${audit.log.retention-period}")
    private int retentionPeriodMonths;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanUpOldLogs()
    {
        LocalDateTime cutoffDate = LocalDateTime.now().minus(retentionPeriodMonths, ChronoUnit.MONTHS);
        auditLogRepository.deleteByChangedAtBefore(cutoffDate);
    }

    public Page<AuditLogEntity> findAuditLogs(String entityName, String newValue, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable)
    {
        return auditLogRepository.findAuditLogs(entityName, newValue, startDate, endDate, pageable);
    }
}
