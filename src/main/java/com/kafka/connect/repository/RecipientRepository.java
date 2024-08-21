package com.kafka.connect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.RecipientEntity;

public interface RecipientRepository extends JpaRepository<RecipientEntity, Long>
{
    List<RecipientEntity> findByIsActiveTrue();

}
