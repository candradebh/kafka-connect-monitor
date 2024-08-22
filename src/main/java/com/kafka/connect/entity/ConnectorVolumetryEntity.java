package com.kafka.connect.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "connector_volumetry")
@Getter
@Setter
public class ConnectorVolumetryEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCliente;

    private String sourceName;

    private String sinkName;

    private Date dataBusca;

    private String tabela;

    private long postgres;

    private long bigquery;

    private long difference;

    private String status;

    @Column(columnDefinition = "TEXT")
    private String querySource;

    @Column(columnDefinition = "TEXT")
    private String querySink;

    @ManyToOne
    @JoinColumn(name = "soucerconnector_id", nullable = false)
    private ConnectorConfigEntity sourceConnector;

    @ManyToOne
    @JoinColumn(name = "sinkconnector_id", nullable = false)
    private ConnectorConfigEntity sinkConnector;

    @ManyToOne
    @JoinColumn(name = "notificationlog_id", nullable = true)
    private NotificationLogEntity notificationLog;

}
