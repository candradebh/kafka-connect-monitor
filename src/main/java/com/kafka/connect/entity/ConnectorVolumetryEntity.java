package com.kafka.connect.entity;

import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    @Column(columnDefinition = "TEXT")
    private String querySource;

    @Column(columnDefinition = "TEXT")
    private String querySink;

    @ManyToOne
    @JoinColumn(name = "soucerconnector_id", nullable = false)
    private ConnectorConfigEntity sourceConnector;

}
