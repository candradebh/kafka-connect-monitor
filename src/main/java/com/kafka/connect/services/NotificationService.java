package com.kafka.connect.services;

import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.NotificationLogEntity;
import com.kafka.connect.repository.NotificationLogRepository;

/**
 * Servico que cria as notificacoes na tabela e associa a volumetria notificada
 */
@Service("NotificationService")
public class NotificationService implements SchedulableTask
{

    @Autowired
    private NotificationLogRepository logRepository;

    @Override
    public void execute()
    {
        this.notifyUsers();
    }

    @Transactional
    private void notifyUsers()
    {
        List<NotificationLogEntity> v_listaEnviar = logRepository.findByFoiEnviadoFalseAndTemErroFalse();
        System.out.println(v_listaEnviar.size());
    }

}
