package com.kafka.connect.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.LogEntity;
import com.kafka.connect.services.LogService;

@RestController
@RequestMapping("/logs")
public class LogController
{

    @Autowired
    private LogService logService;

    @PostMapping
    public ResponseEntity<LogEntity> createLog(@RequestParam String eventName, @RequestParam String description)
    {
        LogEntity log = logService.createLog(eventName, description);
        return ResponseEntity.ok(log);
    }

    @GetMapping
    public ResponseEntity<List<LogEntity>> getAllLogs()
    {
        List<LogEntity> logs = logService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntity> getLogById(@PathVariable Long id)
    {
        LogEntity log = logService.getLogById(id);
        return ResponseEntity.ok(log);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id)
    {
        logService.deleteLog(id);
        return ResponseEntity.noContent().build();
    }
}
