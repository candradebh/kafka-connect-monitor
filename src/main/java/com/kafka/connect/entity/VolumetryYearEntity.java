package com.kafka.connect.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "volumetry_year")
@Getter
@Setter
public class VolumetryYearEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteNome;

    private String nomeTabela;

    private int ano;

    private int mes;

    private long totalRecordsPostgres;

    private long totalRecordsBigquery;

}
