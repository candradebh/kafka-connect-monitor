package com.kafka.connect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.dto.ConnectorSummaryDTO;
import com.kafka.connect.entity.ConnectorConfigEntity;

public interface ConnectorConfigRepository extends JpaRepository<ConnectorConfigEntity, Long>
{
    Optional<ConnectorConfigEntity> findByName(String name);

    List<ConnectorConfigEntity> findByNameIn(List<String> names);

    List<ConnectorConfigEntity> findByType(String type);

    List<ConnectorConfigEntity> findByNomeCliente(String nomeCliente);

    @Query("SELECT c FROM ConnectorConfigEntity c WHERE c.type = :type AND c.nomeCliente = :nomeCliente")
    Optional<ConnectorConfigEntity> findByTypeAndNomeCliente(@Param("type") String type, @Param("nomeCliente") String nomeCliente);

    @Query("SELECT new com.kafka.connect.dto.ConnectorSummaryDTO(c.nomeCliente, COUNT(c)) " + "FROM ConnectorConfigEntity c GROUP BY c.nomeCliente")
    List<ConnectorSummaryDTO> findAllGroupedByClientName();

    @Modifying
    @Query("DELETE FROM ConnectorConfigEntity c WHERE c.name IN :names")
    void deleteAllByNameIn(@Param("names") List<String> names);

}
