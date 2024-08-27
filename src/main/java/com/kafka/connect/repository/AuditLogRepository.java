package com.kafka.connect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.AuditLogEntity;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long>
{

    List<AuditLogEntity> findByEntityNameAndEntityId(String entityName, Long entityId);

}
