package com.kafka.connect.entity;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
