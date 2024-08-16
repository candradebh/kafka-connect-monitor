package com.kafka.connect.services;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kafka.connect.entity.VolumetryRowsEntity;
import com.kafka.connect.repository.VolumetryRowsRepository;

/**
 * Servico que apaga os registro na bigquery automaticamente
 * 
 * @author calbertoma
 */

@Service
public class VolumetryDeleteRowsBigqueryService
{
    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    @Autowired
    private VolumetryRowsRepository volumetryRowsRepository;

    @Autowired
    private BigQueryService bigqueryService;

    @Transactional
    public void deleteRowsInBigquery()
    {
        logger.info("Delete Rows In BigQuery - Inicio");
        LocalDate today = LocalDate.now();
        LocalDate v_dataLimiteApagar = today.minusDays(2);

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

                    System.out.println(v_query);

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

        logger.info("Data Monitor - Finalizado");
    }

}
