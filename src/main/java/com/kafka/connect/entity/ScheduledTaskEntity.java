package com.kafka.connect.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "scheduled_tasks")
public class ScheduledTaskEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String serviceName;

    @Column(nullable = false)
    private String cronExpression;

    @Column(nullable = false)
    private String description;

    @Column(nullable = true)
    private LocalDateTime lastExecutionTime;

    public ScheduledTaskEntity(Long id, String serviceName, String cronExpression, String description)
    {
        this.id = id;
        this.serviceName = serviceName;
        this.cronExpression = cronExpression;
        this.description = description;
    }

    // Getters e Setters
}
