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

@Configuration
public class InitialDataLoader
{

    private static final Logger logger = Logger.getLogger(InitialDataLoader.class.getName());

    /**
     * Exemplo Cron:
     * <ul>
     * <li>12 em 12 horas = 0 0 0/12 * * ?</li>
     * <li>Meia noite = 0 0 0 * * ?</li>
     * </ul>
     **/
    @Bean
    public CommandLineRunner loadData(final ScheduledTaskRepository repository, final DynamicScheduledTaskService taskService)
    {
        return new CommandLineRunner()
        {
            @Override
            public void run(String... args) throws Exception
            {

                List<ScheduledTaskEntity> scheduledTasks = new ArrayList<ScheduledTaskEntity>();

                // Criar e salvar tarefas iniciais
                scheduledTasks
                    .add(new ScheduledTaskEntity(null, "VolumetryDeleteRowsBigqueryService", "0 0 1 * * ?", "Executa todos os dias à 1 hora da manhã", true));
                scheduledTasks.add(new ScheduledTaskEntity(null, "DataMonitoringService", "0 20 21 * * ?",
                    "Executa à meia-noite todos os dias e consulta a volumetria do source e do sink", true));
                scheduledTasks.add(new ScheduledTaskEntity(null, "KafkaConnectorStatusService", "0 */10 * * * ?",
                    "Executa todos os dias de 10 em 10 minutos monitorando o status dos conectores kafka", true));
                scheduledTasks.add(new ScheduledTaskEntity(null, "NotificationService", "0 */10 * * * ?",
                    "Envia notificações de 10 em 10 minutos caso seja necessário", true));

                for (ScheduledTaskEntity scheduledTask : scheduledTasks)
                {
                    Optional<ScheduledTaskEntity> existingTask = repository.findByServiceName(scheduledTask.getServiceName());

                    if (existingTask.isPresent() == false)
                    {
                        logger.info("Cadastrando a Task: " + scheduledTask.getServiceName());
                        repository.save(scheduledTask); // Criar o cadastro

                        logger.info("Agendando a Task no sistema: " + scheduledTask.getServiceName());
                        taskService.scheduleTask(scheduledTask); // Agendar as tarefas
                    }
                }
            }
        };
    }
}
