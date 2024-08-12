package com.kafka.connect.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.bigquery.TableResult;
import com.kafka.connect.datasources.DatabaseConnectionJdbc;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ConnectorVolumetryEntity;
import com.kafka.connect.entity.TableMetadataEntity;
import com.kafka.connect.repository.ConnectorConfigRepository;
import com.kafka.connect.repository.ConnectorVolumetryRepository;
import com.kafka.connect.repository.TableMetadataRepository;

@Service
public class DataMonitoringService
{
    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    @Autowired
    private ConnectorConfigRepository connectorConfigRepository;

    @Autowired
    private ConnectorVolumetryRepository connectorVolumetryRepository;

    @Autowired
    private TableMetadataRepository tableMetadaRepository;

    @Autowired
    private ConnectorConfigService connectorConfigService;

    @Autowired
    private BigQueryService bigqueryService;

    @Transactional
    public void dataMonitor()
    {
        logger.info("Iniciando Data Monitor");

        // atualizar os conectores com os dados mais recentes que estão no kafka
        connectorConfigService.getAndSaveConnectorConfig();

        logger.info("Iniciando a volumetria dos conectores sources...");
        this.atualizarVolumetriaSourceConnectors();

        logger.info("Iniciando a volumetria dos conectores sinks...");
        this.atualizarVolumetriaSinkConnectors();

        logger.info("Data Monitor Finalizado");
    }

    private void atualizarVolumetriaSourceConnectors()
    {

        List<ConnectorConfigEntity> listaConnectors = connectorConfigRepository.findByType("source");
        for (ConnectorConfigEntity connector : listaConnectors)
        {
            List<String> v_tableList = Arrays.asList(connector.getTableIncludeList().split(","));

            DatabaseConnectionJdbc v_databaseConnectionJdbc = new DatabaseConnectionJdbc(connector);

            // buscar o sink do cliente e atualizar no cadastro da volumetria
            Optional<ConnectorConfigEntity> v_optionalSinkCliente = connectorConfigRepository.findByTypeAndNomeCliente("sink", connector.getNomeCliente());
            if (v_optionalSinkCliente.isPresent())
            {
                ConnectorConfigEntity v_sinkCliente = v_optionalSinkCliente.get();

                for (final String v_table : v_tableList)
                {

                    List<ConnectorVolumetryEntity> v_volumetrias = connectorVolumetryRepository.findBySourceName(connector.getName());
                    ConnectorVolumetryEntity v_volumetry = v_volumetrias.stream().filter(new Predicate<ConnectorVolumetryEntity>()
                    {
                        public boolean test(ConnectorVolumetryEntity vol)
                        {
                            return vol != null && vol.getTabela().equals(v_table);
                        }
                    }).findFirst().orElse(new ConnectorVolumetryEntity());

                    v_volumetry.setSourceName(connector.getName());
                    v_volumetry.setSinkName(v_sinkCliente.getName());

                    v_volumetry.setNomeCliente(connector.getNomeCliente());

                    v_volumetry.setSourceConnector(connector);
                    v_volumetry.setDataBusca(new Date());
                    v_volumetry.setTabela(v_table);

                    // montar a query
                    String v_nomeColunaData = this.obterNomeColunaData(v_table);
                    String v_formattedDate = this.obterDataOntemParaBuscar();
                    String v_sql = "SELECT COUNT(*) as total FROM " + v_table;
                    if (v_nomeColunaData != null && v_formattedDate != null)
                    {
                        v_sql += " WHERE " + v_nomeColunaData + " < '" + v_formattedDate + "'";
                    }

                    v_volumetry.setQuerySource(v_sql);
                    v_volumetry.setPostgres(v_databaseConnectionJdbc.getTotalRows(v_volumetry.getQuerySource(), "total"));

                    connectorVolumetryRepository.save(v_volumetry);
                }

            }
            v_databaseConnectionJdbc.close();

        }
    }

    private void atualizarVolumetriaSinkConnectors()
    {

        List<ConnectorConfigEntity> listaConnectors = connectorConfigRepository.findByType("sink");
        for (ConnectorConfigEntity connector : listaConnectors)
        {
            List<ConnectorVolumetryEntity> v_volumetrias = connectorVolumetryRepository.findByNomeCliente(connector.getNomeCliente());

            for (ConnectorVolumetryEntity v_volumetry : v_volumetrias)
            {
                String v_nomeTabelaBigQuery = this.obterTabelaBigquery(connector, v_volumetry);
                String v_query = "SELECT COUNT(*) FROM " + v_nomeTabelaBigQuery;
                String v_nomeColunaData = this.obterNomeColunaData(v_volumetry.getTabela());
                String v_formattedDate = this.obterDataOntemParaBuscar();

                if (v_nomeColunaData != null && v_formattedDate != null)
                {
                    String formattedDate = this.obterDataOntemParaBuscar();
                    v_query += " WHERE " + v_nomeColunaData + " < '" + formattedDate + "'";
                }

                try
                {
                    TableResult v_resultado = bigqueryService.executeQuery(connector.getNomeCliente() + ".json", v_query);

                    if (v_resultado != null)
                    {
                        long v_totalRows = v_resultado.iterateAll().iterator().next().get(0).getLongValue();
                        v_volumetry.setBigquery(v_totalRows);
                        v_volumetry.setQuerySink(v_query);
                        connectorVolumetryRepository.save(v_volumetry);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private String obterTabelaBigquery(ConnectorConfigEntity connector, ConnectorVolumetryEntity v_volumetry)
    {
        return connector.getDefaultDataset() + "." + v_volumetry.getTabela().replace("public.", "_");
    }

    private String obterDataOntemParaBuscar()
    {
        // data de ontem
        LocalDate yesterday = LocalDate.now().minusDays(2);
        LocalDateTime startOfDayYesterday = yesterday.atStartOfDay();// inico as 00:00

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = startOfDayYesterday.format(formatter);
        return formattedDate;
    }

    private String obterNomeColunaData(final String v_table)
    {
        String v_nomeColunaData = null;
        Optional<TableMetadataEntity> v_tableMetaDataOptional = tableMetadaRepository.findByTableName(v_table);
        if (v_tableMetaDataOptional.isPresent())
        {
            TableMetadataEntity v_tableMetaData = v_tableMetaDataOptional.get();
            v_nomeColunaData = v_tableMetaData.getDateColumnName();
        }
        return v_nomeColunaData;
    }
}
