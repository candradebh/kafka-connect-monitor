package com.kafka.connect.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "connector_config")
@Getter
@Setter
@NoArgsConstructor
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

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private CustomerEntity customer;

    @JsonIgnore
    @OneToMany(mappedBy = "connector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectorStatusEntity> statuses = new ArrayList<ConnectorStatusEntity>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "sourceConnector", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConnectorVolumetryEntity> volumetries = new ArrayList<ConnectorVolumetryEntity>();

}
