package com.kafka.connect.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.TableMetadataEntity;
import com.kafka.connect.repository.TableMetadataRepository;

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

    /**
     * Não adicionar tabelas com colunas nullable
     */
    public void insertInitialData()
    {
        List<TableMetadataEntity> initialData = Arrays.asList( //
            new TableMetadataEntity(null, "public.atendimento", "dataCriacaoServidor", true), //
            new TableMetadataEntity(null, "public.campoformulario", "", false), //
            new TableMetadataEntity(null, "public.camporesposta", "", false), //
            new TableMetadataEntity(null, "public.customfields", "", false), //
            new TableMetadataEntity(null, "public.customfieldconfiguration", "", false), //
            new TableMetadataEntity(null, "public.customfieldoption", "", false), //
            new TableMetadataEntity(null, "public.customfieldvalue", "", false), //
            new TableMetadataEntity(null, "public.empresa", "", false), //
            new TableMetadataEntity(null, "public.formulario", "", false), //
            new TableMetadataEntity(null, "public.grupopontoatendimento", "", false), //
            new TableMetadataEntity(null, "public.grupopontoatendimentoponto", "dataassociacao", false), //
            new TableMetadataEntity(null, "public.naoconformidade", "", false), //
            new TableMetadataEntity(null, "public.opcaocampoenumeracao", "", false), //
            new TableMetadataEntity(null, "public.ordemcampoformulario", "", false), //
            new TableMetadataEntity(null, "public.ordemservico", "dataCriacao", true), //
            new TableMetadataEntity(null, "public.planejamentovisita", "", false), //
            new TableMetadataEntity(null, "public.planoacao", "dataCriacao", false), //
            new TableMetadataEntity(null, "public.planoacao_naoconformidade", "", false), //
            new TableMetadataEntity(null, "public.planoacao_ordemservico", "", false), //
            new TableMetadataEntity(null, "public.planoacao_planejamentovisita", "", false), //
            new TableMetadataEntity(null, "public.pontoatendimento", "dataCriacao", true), //
            new TableMetadataEntity(null, "public.regional", "", false), //
            new TableMetadataEntity(null, "public.resposta", "", false), //
            new TableMetadataEntity(null, "public.statusatendimento", "", false), //
            new TableMetadataEntity(null, "public.statusplanoacao", "", false), //
            new TableMetadataEntity(null, "public.statusnaoconformidade", "", false), //
            new TableMetadataEntity(null, "public.statusordemservico", "", false), //
            new TableMetadataEntity(null, "public.tipocampoenumeracao", "", false), //
            new TableMetadataEntity(null, "public.tiponaoconformidade", "", false), //
            new TableMetadataEntity(null, "public.tipovisita", "", false), //
            new TableMetadataEntity(null, "public.unidadeatendimento", "", false), //
            new TableMetadataEntity(null, "public.usuario", "ultimaatualizacao", false), //
            new TableMetadataEntity(null, "public.versaoformulario", "ultimaatualizacao", false) //
        );

        for (TableMetadataEntity data : initialData)
        {
            Optional<TableMetadataEntity> existingRecord = repository.findByTableName(data.getTableName());

            if (existingRecord.isPresent())
            {
                TableMetadataEntity recordToUpdate = existingRecord.get();
                recordToUpdate.setDateColumnName(data.getDateColumnName());
                recordToUpdate.setVolumetryData(data.isVolumetryData());
                repository.save(recordToUpdate);
            }
            else
            {
                repository.save(data);
            }
        }

    }

}
