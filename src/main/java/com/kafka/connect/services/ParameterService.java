package com.kafka.connect.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.ParametersEntity;
import com.kafka.connect.repository.ParameterRepository;
import jakarta.annotation.PostConstruct;

@Service
public class ParameterService
{

    @Autowired
    private ParameterRepository repository;

    @PostConstruct
    public void init()
    {
        this.insertInitialData();
    }

    public void insertInitialData()
    {
        List<ParametersEntity> initialData = Arrays.asList( //

            new ParametersEntity(null, "name", "Nome do Conector", "all", "", "Nome do connector"), //
            new ParametersEntity(null, "connector.class", "Classe do Conector", "all", "", "Nome da classe java do connector"), //
            new ParametersEntity(null, "defaultDataset", "Dataset Bigquery", "sink", "", "Nome do dataset que irá receber os dados no destino"), //
            new ParametersEntity(null, "projectBigquery", "Projeto Bigquery", "sink", "", "Nome do prrojeto que irá receber os dados no destino"), //
            new ParametersEntity(null, "mergeIntervalMs", "Merge Interval (ms)", "sink", "60.000",
                "O intervalo de tempo em milissegundos entre operações de mesclagem, essa config sobrepõe todas as outras de limite"), //
            new ParametersEntity(null, "database.dbname", "Banco de Dados", "source", "", "Nome do banco de dados de origem"), //
            new ParametersEntity(null, "database.port", "Porta do banco", "source", "", "Numero da porta de conexão no banco de dados de origem"), //
            new ParametersEntity(null, "database.hostname", "Host/IP", "source", "", "Host ou IP de conexão no banco de dados de origem"), //
            new ParametersEntity(null, "database.user", "Usuario do Banco", "source", "", "Usuário de conexão no banco de dados de origem"), //
            new ParametersEntity(null, "database.password", "Senha", "source", "", "Senha de conexão no banco de dados de origem"), //
            new ParametersEntity(null, "table.include.list", "Lista Tabelas", "source", "", "Lista de tabelas na origem que o conector deve sincronizar."));

        for (ParametersEntity data : initialData)
        {
            Optional<ParametersEntity> existingRecord = repository.findByName(data.getName());

            if (existingRecord.isPresent())
            {
                ParametersEntity recordToUpdate = existingRecord.get();
                recordToUpdate.setName(data.getName());
                recordToUpdate.setTitle(data.getTitle());
                recordToUpdate.setType(data.getType());
                recordToUpdate.setDefaultValue(data.getDefaultValue());
                recordToUpdate.setDescription(data.getDescription());

                repository.save(recordToUpdate);
            }
            else
            {
                repository.save(data);
            }
        }

    }

}
