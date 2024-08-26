package com.kafka.connect.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ConnectorStatusEntity;
import com.kafka.connect.repository.ConnectorConfigRepository;
import com.kafka.connect.repository.ConnectorStatusRepository;

@RestController
@RequestMapping("/status")
public class ConnectorStatusController
{

    @Autowired
    private ConnectorStatusRepository repository;

    @Autowired
    private ConnectorConfigRepository connectorConfigRepository;

    @GetMapping
    public ResponseEntity<List<ConnectorStatusEntity>> getAllStatus()
    {
        List<ConnectorStatusEntity> list = repository.findAll();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ConnectorStatusEntity>> getConnectorStatus(@PathVariable("id") Long id)
    {
        Optional<ConnectorConfigEntity> v_connectorOpt = connectorConfigRepository.findById(id);
        if (v_connectorOpt.isPresent())
        {
            ConnectorConfigEntity v_connector = v_connectorOpt.get();
            List<ConnectorStatusEntity> v_list = repository.findByConnector(v_connector);
            if (v_list != null)
            {

                return ResponseEntity.ok(v_list);
            }
            else
            {

            }
        }
        return ResponseEntity.notFound().build();

    }

}
