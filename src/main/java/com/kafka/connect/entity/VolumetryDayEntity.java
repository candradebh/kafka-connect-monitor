package com.kafka.connect.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "volumetry_hour")
@Getter
@Setter
public class VolumetryDayEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteNome;

    private String nomeTabela;

    private int ano;

    private int mes;

    private int dia;

    private int hora;

    private long totalRecordsPostgres;

    private long totalRecordsBigquery;

}
