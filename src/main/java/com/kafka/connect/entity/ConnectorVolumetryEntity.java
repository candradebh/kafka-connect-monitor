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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.kafka.connect.annotations.Auditable;
import com.kafka.connect.annotations.AuditableField;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "connector_volumetry")
@Getter
@Setter
@Auditable
public class ConnectorVolumetryEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @AuditableField
    private String nomeCliente;

    private String sourceName;

    private String sinkName;

    private Date dataBusca;

    @AuditableField
    private String tabela;

    @AuditableField
    private long postgres;

    @AuditableField
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

    @JsonManagedReference
    @OneToMany(mappedBy = "connectorVolumetry", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataBusca DESC") // Ordenar pelos registros mais recentes
    private List<ConnectorVolumetryHistoryEntity> volumetryHistory = new ArrayList<>();

    // Método para adicionar um novo registro de histórico e garantir que a lista tenha no máximo 10 registros
    public void addVolumetryHistory(ConnectorVolumetryHistoryEntity history)
    {
        volumetryHistory.add(0, history); // Adiciona o novo registro no início
        if (volumetryHistory.size() > 10)
        {
            volumetryHistory.remove(volumetryHistory.size() - 1); // Remove o registro mais antigo
        }
    }

}
