package com.kafka.connect.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.kafka.connect.entity.ScheduledTaskEntity;
import com.kafka.connect.repository.ScheduledTaskRepository;
import com.kafka.connect.services.DynamicScheduledTaskService;
import com.kafka.connect.services.KafkaConnectorStatusService;

@Configuration
public class InitialDataLoader
{
    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    /**
     * Exemplo Cron: <br>
     * <ul>
     * <li>12 em 12 horas = 0 0 0/12 * * ?</li>
     * <li>meia noite = 0 0 0 * * ?</li>
     * </ul>
     **/
    @Bean
    public CommandLineRunner loadData(final ScheduledTaskRepository repository, final DynamicScheduledTaskService taskService)
    {
        return new CommandLineRunner()
        {
            public void run(String... args) throws Exception
            {

                List<ScheduledTaskEntity> v_listSheduledTasks = new ArrayList<ScheduledTaskEntity>();
                // Criar e salvar tarefas iniciais //0 0 0/12 * * ?
                v_listSheduledTasks
                    .add(new ScheduledTaskEntity(null, "VolumetryDeleteRowsBigqueryService", "0 0 1 * * ?", "Executa todos os dias de 12 em 12 horas"));
                v_listSheduledTasks.add(new ScheduledTaskEntity(null, "DataMonitoringService", "0 0 0 * * ?",
                    "Executa à meia-noite todos os dias e consulta a volumetria do source e do sink"));// 0 0 0 * * ?
                v_listSheduledTasks.add(new ScheduledTaskEntity(null, "KafkaConnectorStatusService", "0 */10 * * * ?",
                    "Executa todos os dias de 10 em 10 minutos monitorando o status dos conectores kafka"));

                for (ScheduledTaskEntity scheduledTaskEntity : v_listSheduledTasks)
                {
                    ScheduledTaskEntity v_scheduledTaskEntity = null;
                    Optional<ScheduledTaskEntity> v_scheduledOptional = repository.findByServiceName(scheduledTaskEntity.getServiceName());
                    if (v_scheduledOptional.isPresent())
                    {
                        v_scheduledTaskEntity = v_scheduledOptional.get();
                        v_scheduledTaskEntity.setCronExpression(scheduledTaskEntity.getCronExpression());
                        v_scheduledTaskEntity.setDescription(scheduledTaskEntity.getDescription());
                    }
                    else
                    {
                        v_scheduledTaskEntity = scheduledTaskEntity;
                    }

                    logger.info("Cadastrando a Task: " + scheduledTaskEntity.getServiceName());
                    repository.save(v_scheduledTaskEntity);// Criar o cadastro
                    logger.info("Agendando a Task no sistema: " + v_scheduledTaskEntity.getServiceName());
                    taskService.scheduleTask(v_scheduledTaskEntity);// Agendar as tarefas
                }

            }
        };
    }
}
