package com.kafka.connect.aspect;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.connect.annotations.Auditable;
import com.kafka.connect.entity.AuditLogEntity;
import com.kafka.connect.repository.AuditLogRepository;

@Aspect
@Component
public class AuditLogAspect
{

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext;

    private Map<String, JpaRepository> repositoryMap = new HashMap<>();

    @Pointcut("execution(* com.kafka.connect.repository.*.save(..)) || execution(* com.kafka.connect.repository.*.delete(..))")
    private void entityMethods()
    {
    }

    @Around("entityMethods()")
    public Object logEntityChanges(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Object[] args = joinPoint.getArgs();
        Object entity = args[0];

        if (entity.getClass().isAnnotationPresent(Auditable.class))
        {
            String entityName = entity.getClass().getSimpleName();
            Long entityId = null;
            String oldValue = null;
            String newValue = null;
            String operationType = null;

            if (entity instanceof Persistable<?>)
            {
                entityId = (Long) ((Persistable<?>) entity).getId();
            }

            if (joinPoint.getSignature().getName().equals("save"))
            {
                if (entityId == null)
                {
                    operationType = "CREATE";
                }
                else
                {
                    oldValue = this.getEntityAsString(entityId, entityName);
                    operationType = "UPDATE";
                }
                newValue = this.convertEntityToJson(entity);
            }
            else if (joinPoint.getSignature().getName().equals("delete"))
            {
                oldValue = this.convertEntityToJson(entity);
                operationType = "DELETE";
            }

            Object result = joinPoint.proceed();

            if (joinPoint.getSignature().getName().equals("delete"))
            {
                entityId = (Long) ((Persistable<?>) entity).getId();
            }

            this.saveAuditLog(entityName, entityId, operationType, oldValue, newValue);
            return result;
        }
        else
        {
            return joinPoint.proceed();
        }
    }

    @SuppressWarnings("unchecked")
    private String getEntityAsString(Long entityId, String entityName)
    {
        JpaRepository repository = repositoryMap.get(entityName);
        if (repository != null && entityId != null)
        {
            Object entity = repository.findById(entityId).orElse(null);
            if (entity != null)
            {
                return this.convertEntityToJson(entity);
            }
        }
        return "{}"; // Retorna um JSON vazio caso a entidade não seja encontrada
    }

    private String convertEntityToJson(Object entity)
    {
        try
        {
            return objectMapper.writeValueAsString(entity);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "{}"; // Retorna um JSON vazio em caso de erro
        }
    }

    @Autowired
    public void setRepositories(ApplicationContext applicationContext)
    {
        Map<String, JpaRepository> repositories = applicationContext.getBeansOfType(JpaRepository.class);
        for (Map.Entry<String, JpaRepository> entry : repositories.entrySet())
        {
            JpaRepository repository = entry.getValue();
            String entityName = this.getEntityNameFromRepository(repository);
            repositoryMap.put(entityName, repository);
        }
    }

    private String getEntityNameFromRepository(JpaRepository repository)
    {
        // Usa o Spring's ResolvableType para resolver o tipo genérico do repositório
        ResolvableType resolvableType = ResolvableType.forClass(repository.getClass()).as(JpaRepository.class);
        Class<?> entityClass = resolvableType.getGeneric(0).resolve();
        return entityClass != null ? entityClass.getSimpleName() : null;
    }

    private void saveAuditLog(String entityName, Long entityId, String operationType, String oldValue, String newValue)
    {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setOperationType(operationType);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setChangedBy("current_user");
        auditLog.setChangedAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }
}
