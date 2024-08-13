package com.kafka.connect.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "connector_config")
@Getter
@Setter
public class ConnectorConfigEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String nomeCliente;

    private String classname;

    private String type; // souce ou sink

    private String connectionString;

    private String usuario;

    private String senha;

    private String host;

    private String port;

    private String database;

    private String defaultDataset;

    private String projectBigquery;

    private long mergeIntervalMs;

    private String ultimoStatusConector;

    private String ultimoStatusTask1;

    private Date dataUltimoStatus;

    @Column(columnDefinition = "text")
    private String tableIncludeList;

    @JsonIgnore
    @OneToMany(mappedBy = "connector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectorStatusEntity> statuses = new ArrayList<ConnectorStatusEntity>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourceConnector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectorVolumetryEntity> volumetries = new ArrayList<ConnectorVolumetryEntity>();

}
