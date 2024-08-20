package com.kafka.connect.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "volumetry_day")
@Getter
@Setter
public class VolumetryDayEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date dataBusca;

    private String clienteNome;

    private String nomeTabela;

    private int ano;

    private int mes;

    private int dia;

    private int hora;

    private long totalRecordsPostgres;

    private long totalRecordsBigquery;

}
