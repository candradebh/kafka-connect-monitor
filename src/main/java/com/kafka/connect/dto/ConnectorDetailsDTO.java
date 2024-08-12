package com.kafka.connect.dto;

import java.util.List;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ParametersEntity;

public class ConnectorDetailsDTO
{
    private ConnectorConfigEntity connectorConfig;

    private List<ParametersEntity> parameters;

    // Construtores, getters e setters
    public ConnectorDetailsDTO(ConnectorConfigEntity connectorConfig, List<ParametersEntity> parameters)
    {
        this.connectorConfig = connectorConfig;
        this.parameters = parameters;
    }

    public ConnectorConfigEntity getConnectorConfig()
    {
        return connectorConfig;
    }

    public void setConnectorConfig(ConnectorConfigEntity connectorConfig)
    {
        this.connectorConfig = connectorConfig;
    }

    public List<ParametersEntity> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<ParametersEntity> parameters)
    {
        this.parameters = parameters;
    }
}
