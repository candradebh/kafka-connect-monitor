package com.kafka.connect.entity;

import java.util.Date;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "connector_status")
@Getter
@Setter
public class ConnectorStatusEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dataBusca;

    private String connectorState;

    private String connectorWorkerId;

    @ManyToOne
    @JoinColumn(name = "connector_id", nullable = false)
    private ConnectorConfigEntity connector;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "connector_status_id")
    private List<TaskStatusEntity> tasks;

    private String errorReason; // Motivo do erro se aplicável

}
