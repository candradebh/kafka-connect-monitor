package com.kafka.connect.entity;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean isActive;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String error;

    public ScheduledTaskEntity(Long id, String serviceName, String cronExpression, String description, boolean isActive)
    {
        this.id = id;
        this.serviceName = serviceName;
        this.cronExpression = cronExpression;
        this.description = description;
        this.isActive = isActive;

    }

    // Getters e Setters
}
