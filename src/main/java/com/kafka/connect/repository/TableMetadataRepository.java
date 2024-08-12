package com.kafka.connect.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.TableMetadataEntity;

public interface TableMetadataRepository extends JpaRepository<TableMetadataEntity, Long>
{

    Optional<TableMetadataEntity> findByTableName(String tableName);
}
