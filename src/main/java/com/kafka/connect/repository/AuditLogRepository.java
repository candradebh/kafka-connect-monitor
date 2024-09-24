package com.kafka.connect.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.AuditLogEntity;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long>
{

    List<AuditLogEntity> findByEntityName(String entityName);

    List<AuditLogEntity> findByEntityNameAndEntityId(String entityName, Long entityId);

    void deleteByChangedAtBefore(LocalDateTime cutoffDate);

    @Query("SELECT a FROM AuditLogEntity a WHERE " + "(:entityName IS NULL OR a.entityName = :entityName) AND "
        + "(:newValue IS NULL OR a.newValue LIKE %:newValue%) AND " + "(:startDate IS NULL OR a.changedAt >= :startDate) AND "
        + "(:endDate IS NULL OR a.changedAt <= :endDate)")
    Page<AuditLogEntity> findAuditLogs(@Param("entityName") String entityName, @Param("newValue") String newValue, @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate, Pageable pageable);

}
