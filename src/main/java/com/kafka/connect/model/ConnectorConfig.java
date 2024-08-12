package com.kafka.connect.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ConnectorConfig
{
    private String name;

    private String type;

    @JsonProperty("nome.cliente")
    private String nomeCliente;

    @JsonProperty("project")
    private String projectBigquery;

    @JsonProperty("defaultDataset")
    private String defaultDataset;

    @JsonProperty("connector.class")
    private String classname;

    @JsonProperty("database.dbname")
    private String dbname;

    @JsonProperty("database.user")
    private String user;

    @JsonProperty("database.password")
    private String password;

    @JsonProperty("database.hostname")
    private String hostname;

    @JsonProperty("database.port")
    private String port;

    @JsonProperty("table.include.list")
    private String tableIncludeList;

    @JsonProperty("mergeIntervalMs")
    private long mergeIntervalMs;

}
