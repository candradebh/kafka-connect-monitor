package com.kafka.connect.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import com.kafka.connect.services.DataMonitoringService;
import com.kafka.connect.services.KafkaConnectorStatusService;
import com.kafka.connect.services.VolumetryDeleteRowsBigqueryService;

@Configuration
@EnableScheduling
public class SchedulerConfig
{

    private final KafkaConnectorStatusService monitorService;

    private final DataMonitoringService dataMonitorService;

    private final VolumetryDeleteRowsBigqueryService volumetryDeleteRowsBigqueryService;

    public SchedulerConfig(KafkaConnectorStatusService monitorService, DataMonitoringService dataMonitorService,
        VolumetryDeleteRowsBigqueryService volumetryDeleteRowsBigqueryService)
    {
        this.monitorService = monitorService;
        this.dataMonitorService = dataMonitorService;
        this.volumetryDeleteRowsBigqueryService = volumetryDeleteRowsBigqueryService;
    }

    @SuppressWarnings("deprecation")
    @Bean
    public TaskScheduler taskScheduler()
    {
        return new ConcurrentTaskScheduler();
    }

    public void configureTasks(ScheduledTaskRegistrar taskRegistrar)
    {
        taskRegistrar.setTaskScheduler(this.taskScheduler());
    }

    @Scheduled(fixedRate = 600000) // 5 minutos
    public void scheduleConnectorMonitoring()
    {
        // monitorService.monitorConnectors();

    }

    // @Scheduled(cron = "0 0 0 * * ?") // Run once a day at midnight
    @Scheduled(fixedRate = 28800000) // 1 hour = 3600000 | 8 hour = 28800000
    public void scheduleDataMonitoring()
    {
        // dataMonitorService.dataMonitor();
        // volumetryDeleteRowsBigqueryService.deleteRowsInBigquery();
    }

}
