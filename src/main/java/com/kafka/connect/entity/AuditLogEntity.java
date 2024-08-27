package com.kafka.connect.entity;

import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class AuditLogEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;

    private Long entityId;

    private String operationType; // CREATE, UPDATE, DELETE

    private String oldValue;

    private String newValue;

    private String changedBy;

    private LocalDateTime changedAt;

    // Getters and Setters
}
