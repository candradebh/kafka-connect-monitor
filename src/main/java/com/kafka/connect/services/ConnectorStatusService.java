package com.kafka.connect.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.ConnectorStatusEntity;
import com.kafka.connect.repository.ConnectorStatusRepository;

@Service
public class ConnectorStatusService
{
    @Autowired
    private ConnectorStatusRepository repository;

    public List<ConnectorStatusEntity> findAll()
    {
        return repository.findAll();
    }
}
