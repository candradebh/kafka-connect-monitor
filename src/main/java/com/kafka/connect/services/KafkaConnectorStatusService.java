package com.kafka.connect.services;

import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kafka.connect.util.SchedulableTask;

@Service("KafkaConnectorStatusService")
public class KafkaConnectorStatusService implements SchedulableTask
{

    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    @Autowired
    private ConnectorConfigService connectorConfigService;

    public void execute()
    {
        logger.info("Iniciando monitoramento do status dos conectores...");

        connectorConfigService.getAndSaveConnectorConfig();

        logger.info("Monitoramento do status dos conectores finalizado.");
    }

}
