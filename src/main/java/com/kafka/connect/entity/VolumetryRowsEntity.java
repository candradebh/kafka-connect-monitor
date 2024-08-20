package com.kafka.connect.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "volumetry_rows")
@Getter
@Setter
public class VolumetryRowsEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dataBusca;

    private String clienteNome;

    private String nomeTabela;

    private String nomeTabelaBigquery;

    private int ano;

    private int mes;

    private int dia;

    private int hora;

    private int minuto;

    private long oid;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean postgres;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean bigquery;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean deletado;

    private Date dataDeletado;
}
