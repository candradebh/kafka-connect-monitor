package com.kafka.connect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.ConnectorStatusEntity;

public interface ConnectorStatusRepository extends JpaRepository<ConnectorStatusEntity, Long>
{
}
