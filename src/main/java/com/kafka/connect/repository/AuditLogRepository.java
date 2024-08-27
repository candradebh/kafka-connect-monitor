package com.kafka.connect.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.AuditLogEntity;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long>
{

    List<AuditLogEntity> findByEntityName(String entityName);

    List<AuditLogEntity> findByEntityNameAndEntityId(String entityName, Long entityId);

    void deleteByChangedAtBefore(LocalDateTime cutoffDate);
}
