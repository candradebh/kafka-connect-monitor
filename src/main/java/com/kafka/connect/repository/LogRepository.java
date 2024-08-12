package com.kafka.connect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.LogEntity;

public interface LogRepository extends JpaRepository<LogEntity, Long>
{

}
