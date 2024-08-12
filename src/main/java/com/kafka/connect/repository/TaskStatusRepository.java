package com.kafka.connect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.kafka.connect.entity.TaskStatusEntity;

public interface TaskStatusRepository extends JpaRepository<TaskStatusEntity, Long>
{
}
