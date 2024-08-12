package com.kafka.connect.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectorSummaryDTO
{
    private String nomeCliente;

    private Long connectorCount;

    public ConnectorSummaryDTO(String nomeCliente, Long connectorCount)
    {
        this.nomeCliente = nomeCliente;
        this.connectorCount = connectorCount;
    }

}
