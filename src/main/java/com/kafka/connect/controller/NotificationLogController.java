package com.kafka.connect.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.NotificationLogEntity;
import com.kafka.connect.services.NotificationLogService;

@RestController
@RequestMapping("/notificationlogs")
public class NotificationLogController
{

    @Autowired
    private NotificationLogService logService;

    @GetMapping
    public Page<NotificationLogEntity> getLogs(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String nomeCliente, @RequestParam(required = false) String recipient, @RequestParam(required = false) String subject)
    {
        Pageable pageable = PageRequest.of(page, size);
        return logService.getLogs(pageable);
        // return logService.getFilteredLogs(nomeCliente, recipient, subject, pageable);
    }

}
