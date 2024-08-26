package com.kafka.connect.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.entity.ScheduledTaskEntity;
import com.kafka.connect.repository.ScheduledTaskRepository;
import com.kafka.connect.services.DynamicScheduledTaskService;

@RestController
@RequestMapping("/scheduled-tasks")
public class ScheduledTaskController
{

    @Autowired
    private ScheduledTaskRepository scheduledTaskRepository;

    @Autowired
    private DynamicScheduledTaskService dynamicScheduledTaskService;

    @GetMapping
    public ResponseEntity<List<ScheduledTaskEntity>> getAll()
    {
        List<ScheduledTaskEntity> list = scheduledTaskRepository.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody ScheduledTaskEntity taskEntity)
    {
        ScheduledTaskEntity savedTask = scheduledTaskRepository.save(taskEntity);
        try
        {
            dynamicScheduledTaskService.scheduleTask(savedTask);
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Erro" + e.getMessage());
        }
        return ResponseEntity.ok(savedTask);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody ScheduledTaskEntity taskEntity)
    {
        Optional<ScheduledTaskEntity> existingTask = scheduledTaskRepository.findById(id);
        if (existingTask.isPresent())
        {

            try
            {
                ScheduledTaskEntity updatedTask = existingTask.get();
                updatedTask.setServiceName(taskEntity.getServiceName());
                updatedTask.setDescription(taskEntity.getDescription());
                updatedTask.setActive(taskEntity.isActive());

                // garante o cadastro correto
                CronTrigger v_cron = new CronTrigger(taskEntity.getCronExpression());
                updatedTask.setCronExpression(v_cron.getExpression());

                if (updatedTask.isActive())
                {

                    dynamicScheduledTaskService.rescheduleTask(updatedTask);
                }
                else
                {
                    dynamicScheduledTaskService.cancelTask(updatedTask.getServiceName());
                }
                scheduledTaskRepository.save(updatedTask);

                return ResponseEntity.ok(updatedTask);
            }
            catch (Exception e)
            {

                return ResponseEntity.badRequest().body("Erro" + e.getMessage());
            }

        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id)
    {
        Optional<ScheduledTaskEntity> existingTask = scheduledTaskRepository.findById(id);
        if (existingTask.isPresent())
        {
            dynamicScheduledTaskService.cancelTask(existingTask.get().getServiceName());
            scheduledTaskRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{serviceName}")
    public ResponseEntity<ScheduledTaskEntity> getLastExecutionTime(@PathVariable String serviceName)
    {
        Optional<ScheduledTaskEntity> taskOpt = scheduledTaskRepository.findByServiceName(serviceName);
        if (taskOpt.isPresent())
        {
            return ResponseEntity.ok(taskOpt.get());
        }
        else
        {
            return ResponseEntity.notFound().build();
        }
    }

}
