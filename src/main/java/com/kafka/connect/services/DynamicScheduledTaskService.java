package com.kafka.connect.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.ScheduledTaskEntity;
import com.kafka.connect.repository.ScheduledTaskRepository;

@Service
public class DynamicScheduledTaskService
{

    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    private Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<String, ScheduledFuture<?>>();

    @PostConstruct
    public void init()
    {
        try
        {
            List<ScheduledTaskEntity> tasks = scheduledTaskRepository.findAll();
            for (ScheduledTaskEntity task : tasks)
            {
                this.scheduleTask(task);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize scheduled tasks", e);
        }
    }

    public void scheduleTask(final ScheduledTaskEntity taskEntity)
    {
        Runnable task = new Runnable()
        {
            public void run()
            {
                logger.info("Inicio da execução do serviço agendado: " + taskEntity.getServiceName());
                SchedulableTask schedulableTask = (SchedulableTask) applicationContext.getBean(taskEntity.getServiceName());
                schedulableTask.execute();
                taskEntity.setLastExecutionTime(LocalDateTime.now());
                scheduledTaskRepository.save(taskEntity);
                logger.info("Fim da execução do serviço agendado: " + taskEntity.getServiceName() + " em " + taskEntity.getLastExecutionTime());
            }
        };
        ScheduledFuture<?> future = taskScheduler.schedule(task, new CronTrigger(taskEntity.getCronExpression()));
        scheduledTasks.put(taskEntity.getServiceName(), future);
    }

    public void rescheduleTask(ScheduledTaskEntity taskEntity)
    {
        this.cancelTask(taskEntity.getServiceName());
        this.scheduleTask(taskEntity);
    }

    public void cancelTask(String serviceName)
    {
        ScheduledFuture<?> future = scheduledTasks.get(serviceName);
        if (future != null)
        {
            future.cancel(false);
        }
    }

    public void executeTask(String serviceName)
    {
        Optional<ScheduledTaskEntity> taskOpt = scheduledTaskRepository.findByServiceName(serviceName);
        if (taskOpt.isPresent())
        {
            ScheduledTaskEntity task = taskOpt.get();
            task.setLastExecutionTime(LocalDateTime.now());
            scheduledTaskRepository.save(task);
            logger.info("Executando serviço: " + serviceName + " em " + task.getLastExecutionTime());

        }
    }

    private Runnable createTask(final String serviceName)
    {
        return new Runnable()
        {
            public void run()
            {
                DynamicScheduledTaskService.this.executeTask(serviceName);
            }
        };
    }
}
