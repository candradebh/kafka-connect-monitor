package com.kafka.connect.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "history_connector_volumetry")
@Getter
@Setter
public class ConnectorVolumetryHistoryEntity
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private long postgres;

    private long bigquery;

    private Date dataBusca;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "connector_volumetry_id", nullable = false)
    private ConnectorVolumetryEntity connectorVolumetry;
}
