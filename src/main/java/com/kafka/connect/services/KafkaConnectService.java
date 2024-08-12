package com.kafka.connect.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.kafka.connect.model.ConnectorConfig;
import com.kafka.connect.model.ConnectorStatus;

@Service
public class KafkaConnectService
{

    @Value("${kafka.connect.url}")
    private String kafkaConnectUrl;

    private final RestTemplate restTemplate;

    public KafkaConnectService(RestTemplateBuilder restTemplateBuilder)
    {
        restTemplate = restTemplateBuilder.build();
    }

    public List<String> getConnectors()
    {
        String url = kafkaConnectUrl + "/connectors";
        ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<String>>()
        {
        });
        return response.getBody();
    }

    public ConnectorStatus getConnectorStatus(String connectorName)
    {
        String url = kafkaConnectUrl + "/connectors/" + connectorName + "/status";
        ResponseEntity<ConnectorStatus> response = restTemplate.getForEntity(url, ConnectorStatus.class);
        return response.getBody();
    }

    public ConnectorConfig getConnectorConfig(String connectorName)
    {
        String url = kafkaConnectUrl + "/connectors/" + connectorName + "/config";
        ResponseEntity<ConnectorConfig> response = restTemplate.getForEntity(url, ConnectorConfig.class);
        return response.getBody();
    }
}
