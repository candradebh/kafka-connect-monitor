package com.kafka.connect.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DataAnaliseYearDTO
{
    private String clienteNome;

    private String nomeTabela;

    private int year;

    private int month;

    private int day;

    private int hour;

    private long totalRecordsPostgres;

    private long totalRecordsBigquery;

}
