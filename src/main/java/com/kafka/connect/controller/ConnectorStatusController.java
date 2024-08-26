package com.kafka.connect.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<Map<String, Object>> getConnectorStatus(@PathVariable("id") Long id, @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int itemsPerPage, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDesc,
        @RequestParam(required = false) String search, @RequestParam(required = false) String connectorState)
    {

        Optional<ConnectorConfigEntity> connectorOpt = connectorConfigRepository.findById(id);
        if (connectorOpt.isPresent())
        {
            ConnectorConfigEntity connector = connectorOpt.get();

            boolean isSortDesc = "true".equalsIgnoreCase(sortDesc);
            Pageable pageable = PageRequest.of(page, itemsPerPage, isSortDesc ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending());

            Page<ConnectorStatusEntity> statusPage;
            if (search != null && !search.isEmpty())
            {
                statusPage = repository.findByConnectorAndSearchAndConnectorState(connector, search, connectorState, pageable);
            }
            else if (connectorState != null)
            {
                statusPage = repository.findByConnectorAndConnectorState(connector, connectorState, pageable);
            }
            else
            {
                statusPage = repository.findByConnectorAndSearch(connector, search, pageable);

            }

            Map<String, Object> response = new HashMap<>();
            response.put("items", statusPage.getContent());
            response.put("total", statusPage.getTotalElements());

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

}
