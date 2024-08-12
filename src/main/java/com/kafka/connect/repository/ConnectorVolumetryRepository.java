package com.kafka.connect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.ConnectorVolumetryEntity;

public interface ConnectorVolumetryRepository extends JpaRepository<ConnectorVolumetryEntity, Long>
{
    Optional<ConnectorVolumetryEntity> findByTabela(String tabela);

    List<ConnectorVolumetryEntity> findBySourceName(String sourceName);

    List<ConnectorVolumetryEntity> findBySinkName(String sinkName);

    List<ConnectorVolumetryEntity> findByNomeCliente(String nomeCliente);
}
