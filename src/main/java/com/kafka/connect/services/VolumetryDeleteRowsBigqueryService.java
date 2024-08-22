package com.kafka.connect.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kafka.connect.datasources.BigQueryConnection;
import com.kafka.connect.datasources.DatabaseConnectionJdbc;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ConnectorVolumetryEntity;
import com.kafka.connect.entity.VolumetryRowsEntity;
import com.kafka.connect.repository.ConnectorConfigRepository;
import com.kafka.connect.repository.ConnectorVolumetryRepository;
import com.kafka.connect.repository.VolumetryRowsRepository;

/**
 * Servico que apaga os registro na bigquery automaticamente quando ele existir na bigquery e não existir no postgres.
 */

@Service("VolumetryDeleteRowsBigqueryService")
public class VolumetryDeleteRowsBigqueryService implements SchedulableTask
{
    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    @Autowired
    private ConnectorConfigRepository connectorConfigRepository;

    @Autowired
    private VolumetryRowsRepository volumetryRowsRepository;

    @Autowired
    private ConnectorVolumetryRepository connectorVolumetryRepository;

    @Autowired
    private BigQueryConnection bigqueryService;

    @Override
    public void execute()
    {
        // deleta as linhas que sao
        this.deleteRowsInBigquery();

        // quando a tabela não tem data mas pode serr resolvida quando o numero total de registros é viavel
        List<ConnectorVolumetryEntity> v_listaVolumetryConnector = connectorVolumetryRepository.findAll();
        for (ConnectorVolumetryEntity connectorVolumetryEntity : v_listaVolumetryConnector)
        {
            // tem mais registro na bigquery e é viavel iterar sobre todos para achar os erados
            if (connectorVolumetryEntity.getDifference() < 0)
            {
                // obter os registros do postgres
                DatabaseConnectionJdbc v_databaseConnectionJdbc = new DatabaseConnectionJdbc(connectorVolumetryEntity.getSourceConnector());
                List<Long> v_listaOidsPostgres = v_databaseConnectionJdbc.getOids("oid", connectorVolumetryEntity.getTabela());

                // obter os registros da bigquery
                String v_nomeTabelaBigQuery = this.obterTabelaBigquery(connectorVolumetryEntity.getSinkConnector(), connectorVolumetryEntity);
                List<Long> v_listaOidsBigquery = bigqueryService.getOids("oid", v_nomeTabelaBigQuery, connectorVolumetryEntity.getNomeCliente());

                // removo os oids do postgres
                v_listaOidsBigquery.removeAll(v_listaOidsPostgres);

                // apagar os registros da bigquery
                StringBuffer v_mensagemErro = new StringBuffer();

                for (Long v_oid : v_listaOidsBigquery)
                {
                    String v_query = "DELETE FROM " + v_nomeTabelaBigQuery + " WHERE oid = " + v_oid;
                    try
                    {
                        bigqueryService.executeQuery(connectorVolumetryEntity.getNomeCliente() + ".json", v_query);
                    }
                    catch (Exception e)
                    {
                        v_mensagemErro.append(e.getMessage());
                        e.printStackTrace();
                    }

                }

                if (v_mensagemErro.length() > 0)
                {
                    logger.warning(v_mensagemErro.toString());
                }
            }
        }
    }

    @Transactional
    public void deleteRowsInBigquery()
    {
        logger.info("Delete Rows In BigQuery - Inicio");
        LocalDate today = LocalDate.now();
        LocalDate v_dataLimiteApagar = today.minusDays(2);

        // busca todos os registros que existem na bigquery e nao existem no postgres
        List<VolumetryRowsEntity> v_listaVolumetryRows = volumetryRowsRepository.findByDeletadoIsFalse();
        for (VolumetryRowsEntity volumetryRowsEntity : v_listaVolumetryRows)
        {
            LocalDate v_dataVolumetry = LocalDate.of(volumetryRowsEntity.getAno(), volumetryRowsEntity.getMes(), volumetryRowsEntity.getDia());
            if (v_dataVolumetry.isBefore(v_dataLimiteApagar) || v_dataVolumetry.isEqual(v_dataLimiteApagar))
            {
                // Deletar o objeto na bigquery
                try
                {

                    String v_query = "DELETE FROM " + volumetryRowsEntity.getNomeTabelaBigquery() + " WHERE oid = " + volumetryRowsEntity.getOid();

                    bigqueryService.executeQuery(volumetryRowsEntity.getClienteNome() + ".json", v_query);

                    volumetryRowsEntity.setDataDeletado(new Date());
                    volumetryRowsEntity.setDeletado(true);

                    volumetryRowsRepository.save(volumetryRowsEntity);

                    logger.info("Query Executada: " + v_query);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        logger.info("Delete Rows In BigQuery - Finalizado");
    }

    public List<Long> obterOidsSomenteNaBigQuery(List<Long> v_listaOidsBigquery, List<Long> v_listaOidsPostgres)
    {
        // Crie uma nova lista baseada na lista do BigQuery
        List<Long> v_listaSomenteNaBigquery = new ArrayList<>(v_listaOidsBigquery);

        // Remova todos os elementos da lista que também existem na lista do PostgreSQL
        v_listaSomenteNaBigquery.removeAll(v_listaOidsPostgres);

        // Agora, v_listaSomenteNaBigquery contém apenas os OIDs que estão na BigQuery, mas não no PostgreSQL
        return v_listaSomenteNaBigquery;
    }

    private String obterTabelaBigquery(ConnectorConfigEntity connector, ConnectorVolumetryEntity v_volumetry)
    {
        return connector.getDefaultDataset() + "." + v_volumetry.getTabela().replace("public.", "_");
    }

}
