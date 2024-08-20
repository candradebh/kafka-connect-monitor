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
import com.kafka.connect.entity.TableMetadataEntity;
import com.kafka.connect.repository.TableMetadataRepository;

@RestController
@RequestMapping("/tablemetadata")
public class TableMetadataController
{

    @Autowired
    private TableMetadataRepository repository;

    @GetMapping
    public ResponseEntity<List<TableMetadataEntity>> getAll()
    {
        List<TableMetadataEntity> list = repository.findAll(Sort.by(Sort.Direction.ASC, "tableName"));
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableMetadataEntity> getLastExecutionTime(@PathVariable Long id)
    {
        Optional<TableMetadataEntity> entityOptional = repository.findById(id);
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
    public ResponseEntity<TableMetadataEntity> createTask(@RequestBody TableMetadataEntity entity)
    {
        TableMetadataEntity savedEntity = repository.save(entity);
        return ResponseEntity.ok(savedEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableMetadataEntity> updateTask(@PathVariable Long id, @RequestBody TableMetadataEntity entity)
    {
        Optional<TableMetadataEntity> existingEntity = repository.findById(id);
        if (existingEntity.isPresent())
        {
            TableMetadataEntity updatedEntity = existingEntity.get();

            updatedEntity.setTableName(entity.getTableName());
            updatedEntity.setDateColumnName(entity.getDateColumnName());
            updatedEntity.setVolumetryData(entity.isVolumetryData());
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
        Optional<TableMetadataEntity> existingEntity = repository.findById(id);
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
