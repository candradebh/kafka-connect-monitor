package com.kafka.connect.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.CustomerEntity;
import com.kafka.connect.repository.ConnectorConfigRepository;
import com.kafka.connect.repository.CustomerRepository;

@RestController
@RequestMapping("/customer")
public class CustomerController
{

    @Autowired
    private CustomerRepository repository;

    @Autowired
    ConnectorConfigRepository connectorConfigRepository;

    @GetMapping
    public ResponseEntity<List<CustomerEntity>> getAll()
    {
        List<CustomerEntity> list = repository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerEntity> getLastExecutionTime(@PathVariable Long id)
    {
        Optional<CustomerEntity> entityOptional = repository.findById(id);
        if (entityOptional.isPresent())
        {
            return ResponseEntity.ok(entityOptional.get());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<CustomerEntity> createTask(@RequestBody CustomerEntity entity)
    {
        CustomerEntity savedEntity = repository.save(entity);
        return ResponseEntity.ok(savedEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerEntity> updateTask(@PathVariable Long id, @RequestBody CustomerEntity entity)
    {
        Optional<CustomerEntity> existingEntity = repository.findById(id);
        if (existingEntity.isPresent())
        {
            CustomerEntity updatedEntity = existingEntity.get();

            updatedEntity.setName(entity.getName());
            updatedEntity.setDescription(entity.getDescription());
            repository.save(updatedEntity);
            return ResponseEntity.ok(updatedEntity);
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id)
    {
        Optional<CustomerEntity> existingEntity = repository.findById(id);
        if (existingEntity.isPresent())
        {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

}
