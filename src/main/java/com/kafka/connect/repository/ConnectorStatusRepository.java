package com.kafka.connect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ConnectorStatusEntity;

public interface ConnectorStatusRepository extends JpaRepository<ConnectorStatusEntity, Long>
{

    List<ConnectorStatusEntity> findByConnector(ConnectorConfigEntity connector);

}
