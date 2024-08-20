package com.kafka.connect.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "table_metadata")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TableMetadataEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableName;

    private String dateColumnName;

    // indica que a tabela deve ter sua volumetria mais detalhada até o delete dos registros no bigquery
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean volumetryData;
}
