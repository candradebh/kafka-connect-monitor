package com.kafka.connect.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.ConnectorStatusEntity;
import com.kafka.connect.services.ConnectorStatusService;

@RestController
@RequestMapping("/status")
public class ConnectorStatusController
{

    @Autowired
    private ConnectorStatusService service;

    @GetMapping
    public ResponseEntity<List<ConnectorStatusEntity>> getAllStatus()
    {
        List<ConnectorStatusEntity> list = service.findAll();
        return ResponseEntity.ok(list);
    }
}
