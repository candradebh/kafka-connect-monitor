package com.kafka.connect.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.VolumetryHourEntity;
import com.kafka.connect.entity.VolumetryRowsEntity;

public interface VolumetryRowsRepository extends JpaRepository<VolumetryRowsEntity, Long>
{

    Optional<VolumetryHourEntity> findByClienteNome(String clienteNome);

}
