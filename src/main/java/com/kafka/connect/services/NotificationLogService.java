package com.kafka.connect.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.NotificationLogEntity;
import com.kafka.connect.repository.NotificationLogRepository;

@Service
public class NotificationLogService
{

    @Autowired
    private NotificationLogRepository logRepository;

    public Page<NotificationLogEntity> getLogs(Pageable pageable)
    {
        return logRepository.findAll(pageable);
    }

    public Page<NotificationLogEntity> getFilteredLogs(String nomeCliente, String recipient, String subject, Pageable pageable)
    {

        // LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate, DateTimeFormatter.ISO_DATE_TIME) : null;
        // LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate, DateTimeFormatter.ISO_DATE_TIME) : null;

        return logRepository.findFilteredLogs(nomeCliente, recipient, subject, pageable);
    }
}
