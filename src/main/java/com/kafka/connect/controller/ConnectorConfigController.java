package com.kafka.connect.controller;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.dto.ConnectorDetailsDTO;
import com.kafka.connect.dto.ConnectorSummaryDTO;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ParametersEntity;
import com.kafka.connect.repository.ParameterRepository;
import com.kafka.connect.services.ConnectorConfigService;

@RestController
@RequestMapping("/connectors")
public class ConnectorConfigController
{
    private static final Logger logger = LoggerFactory.getLogger(ConnectorConfigController.class);

    @Autowired
    private ConnectorConfigService service;

    @Autowired
    private ParameterRepository parameterRepository;

    @GetMapping("/grouped-by-client")
    public ResponseEntity<List<ConnectorSummaryDTO>> getConnectorsGroupedByClientName()
    {
        List<ConnectorSummaryDTO> connectorSummaries = service.getConnectorsGroupedByClientName();
        return ResponseEntity.ok(connectorSummaries);
    }

    @GetMapping
    public ResponseEntity<List<ConnectorConfigEntity>> getAllConnectorConfigs()
    {
        List<ConnectorConfigEntity> connectors = service.findAll();
        return ResponseEntity.ok(connectors);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConnectorDetailsDTO> getConnectorDetails(@PathVariable("id") Long id)
    {
        logger.info("Buscando detalhes do connector com ID: {}", id);
        Optional<ConnectorConfigEntity> connectorConfigEntity = service.getConnectorDetails(id);
        if (connectorConfigEntity.isPresent())
        {
            List<ParametersEntity> parameters = parameterRepository.findAll();
            ConnectorDetailsDTO detailsDTO = new ConnectorDetailsDTO(connectorConfigEntity.get(), parameters);
            logger.info("Connector encontrado: {}", connectorConfigEntity.get());
            return ResponseEntity.ok(detailsDTO);
        }
        else
        {
            logger.warn("Connector não encontrado para o ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/cliente/{nome}")
    public ResponseEntity<List<ConnectorConfigEntity>> getConnectorsForCliente(@PathVariable("nome") String nome)
    {
        List<ConnectorConfigEntity> connectors = service.getConnectorForCliente(nome);
        return ResponseEntity.ok(connectors);
    }

}
