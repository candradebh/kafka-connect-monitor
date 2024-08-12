package com.kafka.connect.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.ConnectorVolumetryEntity;
import com.kafka.connect.services.ConnectorVolumetryService;

@RestController
@RequestMapping("/volumetries")
public class ConnectorVolumetryController
{

    @Autowired
    private ConnectorVolumetryService service;

    @GetMapping
    public ResponseEntity<List<ConnectorVolumetryEntity>> getAllVolumetries()
    {
        List<ConnectorVolumetryEntity> volumetries = service.findAll();
        return ResponseEntity.ok(volumetries);
    }

    @GetMapping("/{nomeCliente}")
    public ResponseEntity<List<ConnectorVolumetryEntity>> getConnectorDetails(@PathVariable("nomeCliente") String nomeCliente)
    {
        List<ConnectorVolumetryEntity> volumetries = service.getVolumetry(nomeCliente);

        return ResponseEntity.ok(volumetries);

    }
}
