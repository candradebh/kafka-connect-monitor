package com.kafka.connect.aspect;

import java.time.LocalDateTime;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;
import com.kafka.connect.annotations.Auditable;
import com.kafka.connect.entity.AuditLogEntity;
import com.kafka.connect.repository.AuditLogRepository;

@Aspect
@Component
public class AuditLogAspect
{

    @Autowired
    private AuditLogRepository auditLogRepository;

    // Definindo um ponto de corte para métodos de salvamento e exclusão nas entidades
    @Pointcut("execution(* com.seuprojeto.repository.*.save(..)) || execution(* com.seuprojeto.repository.*.delete(..))")
    private void entityMethods()
    {
    }

    @Around("entityMethods()")
    public Object logEntityChanges(ProceedingJoinPoint joinPoint) throws Throwable
    {
        Object[] args = joinPoint.getArgs();
        Object entity = args[0];

        // Verifica se a entidade está anotada com @Auditable
        if (entity.getClass().isAnnotationPresent(Auditable.class))
        {
            String entityName = entity.getClass().getSimpleName();
            Long entityId = null;
            String oldValue = null;
            String newValue = null;

            // Recuperar o ID da entidade, se houver
            if (entity instanceof Persistable<?>)
            {
                entityId = (Long) ((Persistable<?>) entity).getId();
            }

            // Antes da execução do método
            if (joinPoint.getSignature().getName().equals("save"))
            {
                oldValue = this.getEntityAsString(entityId, entityName);
                newValue = entity.toString();
            }
            else if (joinPoint.getSignature().getName().equals("delete"))
            {
                oldValue = entity.toString();
            }

            // Executa o método (salvamento ou exclusão)
            Object result = joinPoint.proceed();

            // Após a execução do método
            if (joinPoint.getSignature().getName().equals("delete"))
            {
                entityId = (Long) ((Persistable<?>) entity).getId();
            }

            // Salva o log de auditoria
            this.saveAuditLog(entityName, entityId, joinPoint.getSignature().getName().toUpperCase(), oldValue, newValue);
            return result;
        }
        else
        {
            return joinPoint.proceed();
        }
    }

    private void saveAuditLog(String entityName, Long entityId, String operationType, String oldValue, String newValue)
    {
        AuditLogEntity auditLog = new AuditLogEntity();
        auditLog.setEntityName(entityName);
        auditLog.setEntityId(entityId);
        auditLog.setOperationType(operationType);
        auditLog.setOldValue(oldValue);
        auditLog.setNewValue(newValue);
        auditLog.setChangedBy("current_user"); // Aqui você pode substituir pelo usuário real
        auditLog.setChangedAt(LocalDateTime.now());
        auditLogRepository.save(auditLog);
    }

    private String getEntityAsString(Long entityId, String entityName)
    {
        // Implemente a lógica para buscar a entidade existente e convertê-la em String
        // Por exemplo, você pode usar um repositório para buscar a entidade por ID
        return null;
    }
}
