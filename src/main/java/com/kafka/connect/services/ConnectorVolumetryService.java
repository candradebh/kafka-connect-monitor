package com.kafka.connect.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.ConnectorVolumetryEntity;
import com.kafka.connect.repository.ConnectorVolumetryRepository;

@Service
public class ConnectorVolumetryService
{
    @Autowired
    private ConnectorVolumetryRepository repository;

    public List<ConnectorVolumetryEntity> findAll()
    {
        return repository.findAll();
    }

    public List<ConnectorVolumetryEntity> getVolumetry(String nomeCliente)
    {
        return repository.findByNomeCliente(nomeCliente);
    }
}
