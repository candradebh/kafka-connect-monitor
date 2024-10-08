package com.kafka.connect.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.cloud.bigquery.TableResult;
import com.kafka.connect.datasources.BigQueryConnection;
import com.kafka.connect.datasources.DatabaseConnectionJdbc;
import com.kafka.connect.dto.DataAnaliseYearDTO;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ConnectorVolumetryEntity;
import com.kafka.connect.entity.ConnectorVolumetryHistoryEntity;
import com.kafka.connect.entity.NotificationLogEntity;
import com.kafka.connect.entity.RecipientEntity;
import com.kafka.connect.entity.TableMetadataEntity;
import com.kafka.connect.entity.VolumetryDayEntity;
import com.kafka.connect.entity.VolumetryHourEntity;
import com.kafka.connect.entity.VolumetryMonthDayEntity;
import com.kafka.connect.entity.VolumetryRowsEntity;
import com.kafka.connect.entity.VolumetryYearEntity;
import com.kafka.connect.enums.StatusVolumetry;
import com.kafka.connect.repository.ConnectorConfigRepository;
import com.kafka.connect.repository.ConnectorVolumetryRepository;
import com.kafka.connect.repository.NotificationLogRepository;
import com.kafka.connect.repository.RecipientRepository;
import com.kafka.connect.repository.TableMetadataRepository;
import com.kafka.connect.repository.VolumetryDayRepository;
import com.kafka.connect.repository.VolumetryHourRepository;
import com.kafka.connect.repository.VolumetryMonthRepository;
import com.kafka.connect.repository.VolumetryRowsRepository;
import com.kafka.connect.repository.VolumetryYearRepository;

@Service("DataMonitoringService")
public class DataMonitoringService implements SchedulableTask
{
    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    private static final String m_typeConectorSource = "source";

    private static final String m_typeConectorSink = "sink";

    @Autowired
    private ConnectorConfigRepository connectorConfigRepository;

    @Autowired
    private ConnectorVolumetryRepository connectorVolumetryRepository;

    @Autowired
    private VolumetryYearRepository volumetryYearRepository;

    @Autowired
    private VolumetryMonthRepository volumetryMonthRepository;

    @Autowired
    private VolumetryDayRepository volumetryDayRepository;

    @Autowired
    private VolumetryHourRepository volumetryHourRepository;

    @Autowired
    private VolumetryRowsRepository volumetryRowsRepository;

    @Autowired
    private TableMetadataRepository tableMetadaRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private NotificationLogRepository logRepository;

    @Autowired
    private ConnectorConfigService connectorConfigService;

    @Autowired
    private BigQueryConnection bigqueryService;

    @Override
    public void execute()
    {
        try
        {
            this.dataMonitor();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.warning("ERRO ao executar DataMonitoring: " + e.getMessage());
        }

    }

    @Transactional
    public void dataMonitor()
    {
        logger.info("Data Monitor - Inicio");

        // atualizar os conectores com os dados mais recentes que estão no kafka
        connectorConfigService.getAndSaveConnectorConfig();

        logger.info("Atualizando volumetria com os dados de origem e destino");
        this.atualizarVolumetriaTabelasSourceAndSink();

        logger.info("Data Monitor - Finalizado");
    }

    private void atualizarVolumetriaTabelasSourceAndSink()
    {

        List<ConnectorConfigEntity> listaConnectors = connectorConfigRepository.findByType(m_typeConectorSource);

        DatabaseConnectionJdbc v_databaseConnectionJdbc;

        for (ConnectorConfigEntity connector : listaConnectors)
        {
            // conecta ao Postgres
            v_databaseConnectionJdbc = new DatabaseConnectionJdbc(connector);

            if (connector.getTableIncludeList() != null && connector.getTableIncludeList().isEmpty() == false)
            {

                List<String> v_tableList = Arrays.asList(connector.getTableIncludeList().split(","));

                // buscar o sink do cliente e atualizar no cadastro da volumetria
                Optional<ConnectorConfigEntity> v_optionalSinkCliente = connectorConfigRepository.findByTypeAndNomeCliente("sink", connector.getNomeCliente());
                if (v_optionalSinkCliente.isPresent())
                {
                    ConnectorConfigEntity v_sinkCliente = v_optionalSinkCliente.get();

                    // mensagem ao final de notificacao
                    HashMap<String, Integer> v_hashStatusCount = new HashMap<String, Integer>();
                    List<ConnectorVolumetryEntity> v_listVolumetryError = new ArrayList<ConnectorVolumetryEntity>();

                    for (final String v_table : v_tableList)
                    {
                        TableMetadataEntity v_tableEntity = this.obterDadosTabela(v_table);
                        String v_formattedDate = this.obterDataOntemParaBuscar();

                        // volumetria source
                        ConnectorVolumetryEntity v_volumetry = this.atualizarVolumetryTableSourceConnector(connector, v_databaseConnectionJdbc, v_sinkCliente,
                            v_tableEntity, v_formattedDate, v_table);

                        // volumetria sink
                        String v_nomeTabelaBigQuery = this.obterTabelaBigquery(v_sinkCliente, v_volumetry);
                        this.atualizarVolumetriaTabelaSinkConnector(connector, v_volumetry, v_tableEntity, v_formattedDate, v_nomeTabelaBigQuery);

                        v_volumetry.setDataBusca(new Date());
                        v_volumetry.setDifference(v_volumetry.getPostgres() - v_volumetry.getBigquery());
                        v_volumetry.setStatus(this.calcularStatus(v_volumetry.getDifference()));

                        this.updateCount(v_hashStatusCount, v_volumetry);

                        // salvo em uma lista os 10 ultimos historicos
                        this.updateConnectorVolumetryHistory(v_volumetry);

                        // connectorVolumetryRepository.save(v_volumetry);

                        // lista com erro para criar notificacao
                        if (v_volumetry.getStatus().equals(StatusVolumetry.ERRO.name()))
                        {
                            v_listVolumetryError.add(v_volumetry);
                        }

                        logger.info("Atualizado a volumetria[Nivel 1]: Cliente(" + connector.getNomeCliente() + "), connector(" + connector.getName()
                            + "), tabela(" + v_table + ")");

                        this.atualizacaoVolumetriaAno(v_databaseConnectionJdbc, connector, v_tableEntity, v_volumetry, v_nomeTabelaBigQuery);

                    }

                    // criar notificacao
                    if (v_hashStatusCount != null && v_hashStatusCount.containsKey(StatusVolumetry.ERRO.name())
                        && v_hashStatusCount.get(StatusVolumetry.ERRO.name()).intValue() > 0)
                    {
                        this.createNotification(connector.getNomeCliente(), v_listVolumetryError);
                    }

                }
            }
            else
            {

                // implementar uma busca em todas as tabelas do banco, quem sabe no futuro
            }
            v_databaseConnectionJdbc.close();
        }
    }

    public void updateConnectorVolumetryHistory(ConnectorVolumetryEntity volumetryEntity)
    {
        ConnectorVolumetryHistoryEntity v_history = volumetryEntity.getVolumetryHistory().stream().filter(new Predicate<ConnectorVolumetryHistoryEntity>()
        {
            @Override
            public boolean test(ConnectorVolumetryHistoryEntity vh)
            {
                return vh.getDataBusca().getTime() == volumetryEntity.getDataBusca().getTime();
            }
        }).findFirst().orElse(new ConnectorVolumetryHistoryEntity());

        v_history.setPostgres(volumetryEntity.getPostgres());
        v_history.setBigquery(volumetryEntity.getBigquery());
        v_history.setDataBusca(volumetryEntity.getDataBusca());
        v_history.setConnectorVolumetry(volumetryEntity);

        // Adicionar o registro à entidade principal
        volumetryEntity.addVolumetryHistory(v_history);

        // Salvar a entidade principal e o histórico
        connectorVolumetryRepository.save(volumetryEntity);
    }

    private void updateCount(HashMap<String, Integer> v_hashStatusCount, ConnectorVolumetryEntity connectorVolumetryEntity)
    {
        v_hashStatusCount.put(connectorVolumetryEntity.getStatus(), v_hashStatusCount.getOrDefault(connectorVolumetryEntity.getStatus(), 0) + 1);
    }

    private String calcularStatus(long v_diff)
    {
        if (v_diff > 0)
        {
            return StatusVolumetry.VERIFICAR.name();
        }
        else if (v_diff < 0)
        {
            return StatusVolumetry.ERRO.name();
        }
        else
        {
            return StatusVolumetry.OK.name();
        }
    }

    private void atualizacaoVolumetriaAno(DatabaseConnectionJdbc v_databaseConnectionJdbc, ConnectorConfigEntity connector, TableMetadataEntity v_tableEntity,
        ConnectorVolumetryEntity v_volumetry, String v_nomeTabelaBigQuery)
    {

        // segundo nivel da volumetria que é agrupado por ano e mes, essa busca somente é feita quando a volumetria do prrimeiro nivel nao
        // atende e quando a tabela possui essa analise
        if (v_volumetry.getPostgres() != v_volumetry.getBigquery() && v_tableEntity != null && v_tableEntity.isVolumetryData())
        {

            // SOURCE - ANO E MES
            List<DataAnaliseYearDTO> v_listaDataAnaliseYearPostgresDTO = v_databaseConnectionJdbc.getDataAnaliseYear(v_tableEntity.getTableName(),
                connector.getNomeCliente(), v_tableEntity.getDateColumnName());
            this.atualizarVolumetriaPorTabela(v_tableEntity, v_listaDataAnaliseYearPostgresDTO, m_typeConectorSource);

            // SINK - ANO E MES
            List<DataAnaliseYearDTO> v_listaDataAnaliseYearBigQueryDTO = bigqueryService.getDataAnaliseYear(connector.getNomeCliente() + ".json",
                v_nomeTabelaBigQuery, connector.getNomeCliente(), v_tableEntity.getDateColumnName());
            this.atualizarVolumetriaPorTabela(v_tableEntity, v_listaDataAnaliseYearBigQueryDTO, m_typeConectorSink);

            logger.info("Atualizado a volumetria[Nivel 2]: Cliente(" + connector.getNomeCliente() + "), connector(" + connector.getName() + "), tabela("
                + v_tableEntity.getTableName() + ")");

            // ATUALIZANDO ANO - MES E DIA
            List<VolumetryYearEntity> v_listaVolumetriaAno = volumetryYearRepository.findByClienteNomeTabela(connector.getNomeCliente(),
                v_tableEntity.getTableName());

            for (VolumetryYearEntity volumetryYearEntity : v_listaVolumetriaAno)
            {
                if (volumetryYearEntity.getTotalRecordsPostgres() != volumetryYearEntity.getTotalRecordsBigquery())
                {

                    // SOURCE - MES E DIA
                    List<DataAnaliseYearDTO> v_dadosMesDiaPostgres = v_databaseConnectionJdbc.getDataAnaliseYearMonth(volumetryYearEntity.getNomeTabela(),
                        volumetryYearEntity.getClienteNome(), v_tableEntity.getDateColumnName(), volumetryYearEntity.getAno(), volumetryYearEntity.getMes());
                    this.atualizarVolumetiaMesDia(m_typeConectorSource, v_dadosMesDiaPostgres, v_tableEntity);

                    // SINK - MES E DIA
                    List<DataAnaliseYearDTO> v_dadosMesDiaBigQuery = bigqueryService.getDataAnaliseYearMonth(connector.getNomeCliente() + ".json",
                        v_nomeTabelaBigQuery, v_tableEntity.getDateColumnName(), volumetryYearEntity.getClienteNome(), volumetryYearEntity.getAno(),
                        volumetryYearEntity.getMes());
                    this.atualizarVolumetiaMesDia(m_typeConectorSink, v_dadosMesDiaBigQuery, v_tableEntity);

                    // ATUALIZANDO MES - DIA E HORA
                    List<VolumetryMonthDayEntity> v_listaVolumetriaMes = volumetryMonthRepository.findByClienteNomeTabelaAnoMesDia(connector.getNomeCliente(),
                        v_tableEntity.getTableName(), volumetryYearEntity.getAno(), volumetryYearEntity.getMes());
                    for (VolumetryMonthDayEntity volumetryMonthDayEntity : v_listaVolumetriaMes)
                    {

                        if (volumetryMonthDayEntity.getTotalRecordsPostgres() != volumetryMonthDayEntity.getTotalRecordsBigquery())
                        {

                            // SOURCE - DIA E HORA
                            List<DataAnaliseYearDTO> v_dadosMesDiaHoraPostgres = v_databaseConnectionJdbc.getDataAnaliseYearMonthDay(
                                volumetryMonthDayEntity.getNomeTabela(), volumetryMonthDayEntity.getClienteNome(), v_tableEntity.getDateColumnName(),
                                volumetryMonthDayEntity.getAno(), volumetryMonthDayEntity.getMes(), volumetryMonthDayEntity.getDia());
                            this.atualizarVolumetiaMesDiaHora(m_typeConectorSource, v_dadosMesDiaHoraPostgres, v_tableEntity);

                            // SINK - DIA E HORA
                            List<DataAnaliseYearDTO> v_dadosMesDiaHora = bigqueryService.getDataAnaliseYearMonthDay(connector.getNomeCliente() + ".json",
                                v_nomeTabelaBigQuery, v_tableEntity.getDateColumnName(), volumetryMonthDayEntity.getClienteNome(),
                                volumetryMonthDayEntity.getAno(), volumetryMonthDayEntity.getMes(), volumetryMonthDayEntity.getDia());
                            this.atualizarVolumetiaMesDiaHora(m_typeConectorSink, v_dadosMesDiaHora, v_tableEntity);

                            // ATUALIZANDO DIA - HORA e MINUTOS
                            List<VolumetryDayEntity> v_listaVolumetriaDia = volumetryDayRepository.findByClienteNomeTabelaAnoMesDiaHora(
                                connector.getNomeCliente(), v_tableEntity.getTableName(), volumetryMonthDayEntity.getAno(), volumetryMonthDayEntity.getMes(),
                                volumetryMonthDayEntity.getDia());

                            for (VolumetryDayEntity volumetryDayEntity : v_listaVolumetriaDia)
                            {

                                if (volumetryDayEntity.getTotalRecordsPostgres() != volumetryDayEntity.getTotalRecordsBigquery())
                                {
                                    // SOURCE - DIA E HORA
                                    List<DataAnaliseYearDTO> v_dadosMesDiaHoraMinutosPostgres = v_databaseConnectionJdbc.getDataAnaliseYearMonthDayHour(
                                        volumetryDayEntity.getNomeTabela(), volumetryDayEntity.getClienteNome(), v_tableEntity.getDateColumnName(),
                                        volumetryDayEntity.getAno(), volumetryDayEntity.getMes(), volumetryDayEntity.getDia(), volumetryDayEntity.getHora());
                                    this.atualizarVolumetiaMesDiaHoraMinutes(m_typeConectorSource, v_dadosMesDiaHoraMinutosPostgres, v_tableEntity);

                                    // SINK - DIA E HORA
                                    List<DataAnaliseYearDTO> v_dadosMesDiaHoraMinutosBigquery = bigqueryService.getDataAnaliseYearMonthDayHour(
                                        connector.getNomeCliente() + ".json", v_nomeTabelaBigQuery, v_tableEntity.getDateColumnName(),
                                        volumetryDayEntity.getClienteNome(), volumetryDayEntity.getAno(), volumetryDayEntity.getMes(),
                                        volumetryDayEntity.getDia(), volumetryDayEntity.getHora());
                                    this.atualizarVolumetiaMesDiaHoraMinutes(m_typeConectorSink, v_dadosMesDiaHoraMinutosBigquery, v_tableEntity);

                                    // OBTER OS REGISTROS DE FATO
                                    List<VolumetryHourEntity> v_listaVolumetriaHora = volumetryHourRepository.findByClienteNomeTabelaAnoMesDiaHora(
                                        connector.getNomeCliente(), v_tableEntity.getTableName(), volumetryDayEntity.getAno(), volumetryDayEntity.getMes(),
                                        volumetryDayEntity.getDia(), volumetryDayEntity.getHora());

                                    for (VolumetryHourEntity v_volumetryHourEntity : v_listaVolumetriaHora)
                                    {
                                        // procurar nos minutos os registros que estao errados
                                        if (v_volumetryHourEntity.getTotalRecordsPostgres() != v_volumetryHourEntity.getTotalRecordsBigquery())
                                        {

                                            // SOURCE - DIA E HORA
                                            List<DataAnaliseYearDTO> v_dadosMinutosPostgres = v_databaseConnectionJdbc.getDataAnaliseYearMonthDayHourMinutes(
                                                v_volumetryHourEntity.getNomeTabela(), v_volumetryHourEntity.getClienteNome(),
                                                v_tableEntity.getDateColumnName(), v_volumetryHourEntity.getAno(), v_volumetryHourEntity.getMes(),
                                                v_volumetryHourEntity.getDia(), v_volumetryHourEntity.getHora(), v_volumetryHourEntity.getMinuto());
                                            this.atualizarVolumetiaDosRegistrosErrados(m_typeConectorSource, v_dadosMinutosPostgres, v_tableEntity, "postgres");

                                            // SINK - DIA E HORA
                                            List<DataAnaliseYearDTO> v_dadosMinutosBigquery = bigqueryService.getDataAnaliseYearMonthDayHourMinutes(
                                                connector.getNomeCliente() + ".json", v_nomeTabelaBigQuery, v_tableEntity.getDateColumnName(),
                                                v_volumetryHourEntity.getClienteNome(), v_volumetryHourEntity.getAno(), v_volumetryHourEntity.getMes(),
                                                v_volumetryHourEntity.getDia(), v_volumetryHourEntity.getHora(), v_volumetryHourEntity.getMinuto());
                                            this.atualizarVolumetiaDosRegistrosErrados(m_typeConectorSink, v_dadosMinutosBigquery, v_tableEntity, "bigquery");

                                        }
                                    }

                                    // volumetryRowsRepository

                                }

                            }

                        }

                    }

                }
            }

        }
    }

    private ConnectorVolumetryEntity atualizarVolumetryTableSourceConnector(ConnectorConfigEntity connector, DatabaseConnectionJdbc v_databaseConnectionJdbc,
        ConnectorConfigEntity v_sinkCliente, TableMetadataEntity v_tableEntity, String v_formattedDate, final String p_table)
    {
        List<ConnectorVolumetryEntity> v_volumetrias = connectorVolumetryRepository.findBySourceName(connector.getName());

        ConnectorVolumetryEntity v_volumetry = v_volumetrias.stream().filter(new Predicate<ConnectorVolumetryEntity>()
        {
            @Override
            public boolean test(ConnectorVolumetryEntity vol)
            {
                return vol != null && vol.getTabela().equals(p_table);
            }
        }).findFirst().orElse(new ConnectorVolumetryEntity());

        v_volumetry.setSourceName(connector.getName());
        v_volumetry.setSinkName(v_sinkCliente.getName());
        v_volumetry.setSinkConnector(v_sinkCliente);
        v_volumetry.setNomeCliente(connector.getNomeCliente());

        v_volumetry.setSourceConnector(connector);

        v_volumetry.setDataBusca(new Date());
        v_volumetry.setTabela(p_table);

        // montar a query
        String v_sql = "SELECT COUNT(*) as total FROM " + p_table;
        if (v_tableEntity != null && v_tableEntity.isVolumetryData() && v_tableEntity.getDateColumnName() != null && v_formattedDate != null)
        {
            v_sql += " WHERE " + v_tableEntity.getDateColumnName() + " < '" + v_formattedDate + "'";
        }

        v_volumetry.setQuerySource(v_sql);
        v_volumetry.setPostgres(v_databaseConnectionJdbc.getTotalRows(v_volumetry.getQuerySource(), "total"));

        return v_volumetry;
    }

    private void atualizarVolumetriaTabelaSinkConnector(ConnectorConfigEntity connector, ConnectorVolumetryEntity v_volumetry,
        TableMetadataEntity v_tabelaEntity, String v_formattedDate, String v_nomeTabelaBigQuery)
    {

        String v_query = "SELECT COUNT(*) FROM " + v_nomeTabelaBigQuery;
        // TableMetadataEntity v_tabelaEntity = this.obterDadosTabela(v_volumetry.getTabela());

        if (v_tabelaEntity != null && v_tabelaEntity.isVolumetryData() && v_tabelaEntity.getDateColumnName() != null && v_formattedDate != null)
        {
            String formattedDate = this.obterDataOntemParaBuscar();
            v_query += " WHERE " + v_tabelaEntity.getDateColumnName() + " < '" + formattedDate + "'";
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

    private void atualizarVolumetriaPorTabela(TableMetadataEntity v_tableEntity, List<DataAnaliseYearDTO> v_listaDataAnaliseYearDTO, String p_typeConnector)
    {
        for (DataAnaliseYearDTO v_DataAnaliseYearDTO : v_listaDataAnaliseYearDTO)
        {
            Optional<VolumetryYearEntity> v_volumetryYearEntityOptional = volumetryYearRepository.findByClienteNomeTabelaAnoMes(
                v_DataAnaliseYearDTO.getClienteNome(), v_tableEntity.getTableName(), v_DataAnaliseYearDTO.getYear(), v_DataAnaliseYearDTO.getMonth());

            VolumetryYearEntity v_volumetryEntity = null;
            if (v_volumetryYearEntityOptional.isPresent())
            {
                v_volumetryEntity = v_volumetryYearEntityOptional.get();
            }
            else
            {
                v_volumetryEntity = new VolumetryYearEntity();
                v_volumetryEntity.setClienteNome(v_DataAnaliseYearDTO.getClienteNome());
                v_volumetryEntity.setNomeTabela(v_tableEntity.getTableName());
                v_volumetryEntity.setAno(v_DataAnaliseYearDTO.getYear());
                v_volumetryEntity.setMes(v_DataAnaliseYearDTO.getMonth());

            }

            if (p_typeConnector.equals("source"))
            {

                v_volumetryEntity.setTotalRecordsPostgres(v_DataAnaliseYearDTO.getTotalRecordsPostgres());
            }
            else
            {
                v_volumetryEntity.setTotalRecordsBigquery(v_DataAnaliseYearDTO.getTotalRecordsBigquery());
            }
            v_volumetryEntity.setDataBusca(new Date());
            volumetryYearRepository.save(v_volumetryEntity);

        }
    }

    private void atualizarVolumetiaMesDia(String p_typeConnector, List<DataAnaliseYearDTO> v_listaDadosMesDia, TableMetadataEntity v_tabelaEntity)
    {

        for (DataAnaliseYearDTO v_itemMesDia : v_listaDadosMesDia)
        {
            Optional<VolumetryMonthDayEntity> v_volumetryMonthOptional = volumetryMonthRepository.findByClienteNomeTabelaAnoMesDia(
                v_itemMesDia.getClienteNome(), v_tabelaEntity.getTableName(), v_itemMesDia.getYear(), v_itemMesDia.getMonth(), v_itemMesDia.getDay());

            VolumetryMonthDayEntity v_volumetryMonthEntity = null;
            if (v_volumetryMonthOptional.isPresent())
            {
                v_volumetryMonthEntity = v_volumetryMonthOptional.get();
            }
            else
            {
                v_volumetryMonthEntity = new VolumetryMonthDayEntity();
                v_volumetryMonthEntity.setClienteNome(v_itemMesDia.getClienteNome());
                v_volumetryMonthEntity.setNomeTabela(v_tabelaEntity.getTableName());
                v_volumetryMonthEntity.setAno(v_itemMesDia.getYear());
                v_volumetryMonthEntity.setMes(v_itemMesDia.getMonth());
                v_volumetryMonthEntity.setDia(v_itemMesDia.getDay());
            }

            if (p_typeConnector.equals("source"))
            {

                v_volumetryMonthEntity.setTotalRecordsPostgres(v_itemMesDia.getTotalRecordsPostgres());
            }
            else
            {
                v_volumetryMonthEntity.setTotalRecordsBigquery(v_itemMesDia.getTotalRecordsBigquery());
            }

            v_volumetryMonthEntity.setDataBusca(new Date());

            volumetryMonthRepository.save(v_volumetryMonthEntity);

        }
    }

    private void atualizarVolumetiaMesDiaHora(String p_typeConnector, List<DataAnaliseYearDTO> v_listaDadosMesDiaHora, TableMetadataEntity v_tabelaEntity)
    {

        for (DataAnaliseYearDTO v_itemMesDiaHora : v_listaDadosMesDiaHora)
        {
            Optional<VolumetryDayEntity> v_volumetryDayOptional = volumetryDayRepository.findByClienteNomeTabelaAnoMesDiaHora(v_itemMesDiaHora.getClienteNome(),
                v_tabelaEntity.getTableName(), v_itemMesDiaHora.getYear(), v_itemMesDiaHora.getMonth(), v_itemMesDiaHora.getDay(), v_itemMesDiaHora.getHour());

            VolumetryDayEntity v_volumetryDayEntity = null;

            if (v_volumetryDayOptional.isPresent())
            {
                v_volumetryDayEntity = v_volumetryDayOptional.get();
            }
            else
            {
                v_volumetryDayEntity = new VolumetryDayEntity();
                v_volumetryDayEntity.setClienteNome(v_itemMesDiaHora.getClienteNome());
                v_volumetryDayEntity.setNomeTabela(v_tabelaEntity.getTableName());
                v_volumetryDayEntity.setAno(v_itemMesDiaHora.getYear());
                v_volumetryDayEntity.setMes(v_itemMesDiaHora.getMonth());
                v_volumetryDayEntity.setDia(v_itemMesDiaHora.getDay());
                v_volumetryDayEntity.setHora(v_itemMesDiaHora.getHour());
            }

            if (p_typeConnector.equals("source"))
            {

                v_volumetryDayEntity.setTotalRecordsPostgres(v_itemMesDiaHora.getTotalRecordsPostgres());
            }
            else
            {
                v_volumetryDayEntity.setTotalRecordsBigquery(v_itemMesDiaHora.getTotalRecordsBigquery());

            }

            v_volumetryDayEntity.setDataBusca(new Date());

            try
            {
                volumetryDayRepository.save(v_volumetryDayEntity);
            }
            catch (Exception e)
            {
                System.out.println(v_volumetryDayEntity);
            }

        }
    }

    private void atualizarVolumetiaMesDiaHoraMinutes(String p_typeConnector, List<DataAnaliseYearDTO> v_listaDadosMesDiaHora,
        TableMetadataEntity v_tabelaEntity)
    {

        for (DataAnaliseYearDTO v_itemMesDiaHora : v_listaDadosMesDiaHora)
        {
            Optional<VolumetryHourEntity> v_volumetryDayOptional = volumetryHourRepository.findByClienteNomeTabelaAnoMesDiaHora(
                v_itemMesDiaHora.getClienteNome(), v_tabelaEntity.getTableName(), v_itemMesDiaHora.getYear(), v_itemMesDiaHora.getMonth(),
                v_itemMesDiaHora.getDay(), v_itemMesDiaHora.getHour(), v_itemMesDiaHora.getMinutes());

            VolumetryHourEntity v_volumetryDayEntity = null;

            if (v_volumetryDayOptional.isPresent())
            {
                v_volumetryDayEntity = v_volumetryDayOptional.get();
            }
            else
            {
                v_volumetryDayEntity = new VolumetryHourEntity();
                v_volumetryDayEntity.setClienteNome(v_itemMesDiaHora.getClienteNome());
                v_volumetryDayEntity.setNomeTabela(v_tabelaEntity.getTableName());
                v_volumetryDayEntity.setAno(v_itemMesDiaHora.getYear());
                v_volumetryDayEntity.setMes(v_itemMesDiaHora.getMonth());
                v_volumetryDayEntity.setDia(v_itemMesDiaHora.getDay());
                v_volumetryDayEntity.setHora(v_itemMesDiaHora.getHour());
                v_volumetryDayEntity.setMinuto(v_itemMesDiaHora.getMinutes());
            }

            if (p_typeConnector.equals("source"))
            {

                v_volumetryDayEntity.setTotalRecordsPostgres(v_itemMesDiaHora.getTotalRecordsPostgres());
            }
            else
            {
                v_volumetryDayEntity.setTotalRecordsBigquery(v_itemMesDiaHora.getTotalRecordsBigquery());
            }

            v_volumetryDayEntity.setDataBusca(new Date());

            volumetryHourRepository.save(v_volumetryDayEntity);

        }
    }

    private void atualizarVolumetiaDosRegistrosErrados(String p_typeConnector, List<DataAnaliseYearDTO> v_listaDadosDto, TableMetadataEntity v_tabelaEntity,
        String p_origem)
    {

        for (DataAnaliseYearDTO v_itemDto : v_listaDadosDto)
        {
            Optional<VolumetryRowsEntity> v_volumetryDayOptional = volumetryRowsRepository.findByClienteNomeTabelaAnoMesDiaHora(v_itemDto.getClienteNome(),
                v_tabelaEntity.getTableName(), v_itemDto.getYear(), v_itemDto.getMonth(), v_itemDto.getDay(), v_itemDto.getHour(), v_itemDto.getMinutes(),
                v_itemDto.getOid());

            VolumetryRowsEntity v_volumetryDayEntity = null;

            if (v_volumetryDayOptional.isPresent())
            {
                v_volumetryDayEntity = v_volumetryDayOptional.get();
            }
            else
            {
                v_volumetryDayEntity = new VolumetryRowsEntity();
                v_volumetryDayEntity.setClienteNome(v_itemDto.getClienteNome());
                v_volumetryDayEntity.setNomeTabela(v_tabelaEntity.getTableName());
                v_volumetryDayEntity.setAno(v_itemDto.getYear());
                v_volumetryDayEntity.setMes(v_itemDto.getMonth());
                v_volumetryDayEntity.setDia(v_itemDto.getDay());
                v_volumetryDayEntity.setHora(v_itemDto.getHour());
                v_volumetryDayEntity.setMinuto(v_itemDto.getMinutes());
                v_volumetryDayEntity.setOid(v_itemDto.getOid());

            }

            v_volumetryDayEntity.setDataBusca(new Date());

            if (p_origem.equals("postgres"))
            {
                v_volumetryDayEntity.setPostgres(true);
            }
            if (p_origem.equals("bigquery"))
            {
                v_volumetryDayEntity.setBigquery(true);
                v_volumetryDayEntity.setNomeTabelaBigquery(v_itemDto.getNomeTabelaBigquery());
            }

            volumetryRowsRepository.save(v_volumetryDayEntity);

        }
    }

    private String obterTabelaBigquery(ConnectorConfigEntity connector, ConnectorVolumetryEntity v_volumetry)
    {
        return connector.getDefaultDataset() + "." + v_volumetry.getTabela().replace("public.", "_");
    }

    private String obterDataOntemParaBuscar()
    {
        // data de ontem
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime startOfDayYesterday = yesterday.atStartOfDay();// inico as 00:00

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = startOfDayYesterday.format(formatter);
        return formattedDate;
    }

    private TableMetadataEntity obterDadosTabela(final String v_table)
    {
        TableMetadataEntity v_tableMetaData = null;
        Optional<TableMetadataEntity> v_tableMetaDataOptional = tableMetadaRepository.findByTableName(v_table);
        if (v_tableMetaDataOptional.isPresent())
        {
            v_tableMetaData = v_tableMetaDataOptional.get();
        }
        return v_tableMetaData;
    }

    private void createNotification(String p_nomeCliente, List<ConnectorVolumetryEntity> v_listVolumetryError)
    {
        StringBuffer htmlMsg = new StringBuffer();
        String v_assunto = "Divergências na Volumetria dos dados (Postgres x BigQuery) para o cliente " + p_nomeCliente;

        htmlMsg.append("<h3>" + v_assunto + "</h3> <BR><BR>");
        htmlMsg.append("<table border='1' cellpadding='5' cellspacing='0'>");
        htmlMsg.append("<tr>")//
            .append("<th>Nome da Tabela</th>")//
            .append("<th>Data da Busca</th>")//
            .append("<th>Total no Postgres</th>")//
            .append("<th>Total no BigQuery</th>")//
            .append("<th>Diferença</th>")//
            .append("<th>Status</th>")//
            .append("</tr>");
        boolean v_teveErro = false;
        for (ConnectorVolumetryEntity v_volumetryError : v_listVolumetryError)
        {

            if (this.verificarHistoricoVolumetria(v_volumetryError))
            {
                v_teveErro = true;
                htmlMsg.append("<tr>")//
                    .append("<td>").append(v_volumetryError.getTabela()).append("</td>")//
                    .append("<td>").append(v_volumetryError.getDataBusca()).append("</td>")//
                    .append("<td>").append(v_volumetryError.getPostgres()).append("</td>")//
                    .append("<td>").append(v_volumetryError.getBigquery()).append("</td>")//
                    .append("<td>").append(v_volumetryError.getDifference()).append("</td>")//
                    .append("<td>").append(v_volumetryError.getStatus()).append("</td>")//
                    .append("</tr>");
            }

        }

        if (v_teveErro)
        {

            // criando a notificacao
            NotificationLogEntity v_notificationLog = new NotificationLogEntity();
            v_notificationLog.setRecipient(this.getEmailsAsCommaSeparatedString());
            v_notificationLog.setSubject(v_assunto);
            v_notificationLog.setMessage(htmlMsg.toString());
            v_notificationLog.setNomeCliente(p_nomeCliente);

            logRepository.save(v_notificationLog);
        }

    }

    public boolean verificarHistoricoVolumetria(ConnectorVolumetryEntity connectorVolumetry)
    {
        // Parâmetros de configuração
        int consultasConsecutivasErro = 3; // Quantidade de consultas consecutivas com erro que disparam alarme
        double limiteToleranciaPercentual = 5.0; // Limite de tolerância para erro percentual
        int contagemErrosConsecutivos = 0;

        List<ConnectorVolumetryHistoryEntity> historico = connectorVolumetry.getVolumetryHistory();
        double somaDiferencasPercentuais = 0;
        int totalConsultas = historico.size();

        for (int i = 0; i < totalConsultas - 1; i++)
        {
            ConnectorVolumetryHistoryEntity atual = historico.get(i);
            ConnectorVolumetryHistoryEntity anterior = historico.get(i + 1);

            long registrosPostgresAtual = atual.getPostgres();
            long registrosBigQueryAtual = atual.getBigquery();
            long registrosPostgresAnterior = anterior.getPostgres();
            long registrosBigQueryAnterior = anterior.getBigquery();

            // Calcular diferença percentual entre Postgres e BigQuery
            double diferencaPercentualAtual = Math.abs(registrosPostgresAtual - registrosBigQueryAtual) / (double) registrosPostgresAtual * 100;

            // Calcular diferença percentual entre as últimas consultas para verificar a estabilidade
            double diferencaPercentualEntreConsultas = Math
                .abs((registrosPostgresAtual - registrosPostgresAnterior) - (registrosBigQueryAtual - registrosBigQueryAnterior))
                / (double) registrosPostgresAnterior * 100;

            somaDiferencasPercentuais += diferencaPercentualAtual;

            // Verifica se a diferença está acima da tolerância
            if (diferencaPercentualAtual > limiteToleranciaPercentual)
            {
                contagemErrosConsecutivos++;
            }
            else
            {
                contagemErrosConsecutivos = 0; // Reinicia contagem se estiver dentro da tolerância
            }

            // Verificar se o erro persiste por várias consultas consecutivas
            if (contagemErrosConsecutivos >= consultasConsecutivasErro)
            {
                return true;
            }

            // Caso a variação entre consultas esteja aumentando, sinal de possível problema
            if (diferencaPercentualEntreConsultas > limiteToleranciaPercentual)
            {
                return true;
            }
        }

        // Cálculo da média de diferença percentual
        double mediaDiferencasPercentuais = somaDiferencasPercentuais / totalConsultas;

        return false;
    }

    public String getEmailsAsCommaSeparatedString()
    {
        List<RecipientEntity> v_recipients = recipientRepository.findByIsActiveTrue();
        return v_recipients.stream().map(RecipientEntity::getEmail).collect(Collectors.joining(","));
    }

}
