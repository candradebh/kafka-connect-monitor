package com.kafka.connect.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.ParametersEntity;

public interface ParameterRepository extends JpaRepository<ParametersEntity, Long>
{
    Optional<ParametersEntity> findByName(String name);
}
