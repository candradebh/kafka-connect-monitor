package com.kafka.connect.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ConnectorStatusEntity;

public interface ConnectorStatusRepository extends JpaRepository<ConnectorStatusEntity, Long>
{

    Page<ConnectorStatusEntity> findByConnector(ConnectorConfigEntity connector, Pageable pageable);

    @Query("SELECT c FROM ConnectorStatusEntity c WHERE c.connector = :connector AND "
        + "(c.connector.name LIKE %:search% OR c.connector.nomeCliente LIKE %:search%)")
    Page<ConnectorStatusEntity> findByConnectorAndSearch(@Param("connector") ConnectorConfigEntity connector, @Param("search") String search,
        Pageable pageable);

    Page<ConnectorStatusEntity> findByConnectorAndConnectorState(ConnectorConfigEntity connector, String connectorState, Pageable pageable);

    @Query("SELECT c FROM ConnectorStatusEntity c WHERE c.connector = :connector AND "
        + "(c.connector.name LIKE %:search% OR c.connector.nomeCliente LIKE %:search%) AND "
        + "(c.connectorState = :connectorState OR :connectorState IS NULL)")
    Page<ConnectorStatusEntity> findByConnectorAndSearchAndConnectorState(@Param("connector") ConnectorConfigEntity connector, @Param("search") String search,
        @Param("connectorState") String connectorState, Pageable pageable);
}
