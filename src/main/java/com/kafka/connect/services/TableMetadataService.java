package com.kafka.connect.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.TableMetadataEntity;
import com.kafka.connect.repository.TableMetadataRepository;
import jakarta.annotation.PostConstruct;

@Service
public class TableMetadataService
{

    @Autowired
    private TableMetadataRepository repository;

    @PostConstruct
    public void init()
    {
        this.insertInitialData();
    }

    public void insertInitialData()
    {
        List<TableMetadataEntity> initialData = Arrays.asList( //
            new TableMetadataEntity(null, "public.atendimento", "dataCriacaoServidor"), //
            new TableMetadataEntity(null, "public.planoacao", "dataCriacao"), //
            new TableMetadataEntity(null, "public.pontoatendimento", "dataCriacao")
        );

        for (TableMetadataEntity data : initialData)
        {
            Optional<TableMetadataEntity> existingRecord = repository.findByTableName(data.getTableName());

            if (existingRecord.isPresent())
            {
                TableMetadataEntity recordToUpdate = existingRecord.get();
                recordToUpdate.setDateColumnName(data.getDateColumnName());
                repository.save(recordToUpdate);
            }
            else
            {
                repository.save(data);
            }
        }

    }

}
