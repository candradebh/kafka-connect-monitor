package com.kafka.connect.services;

import java.util.Date;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void execute()
    {
        this.notifyUsers();
    }

    @Transactional
    private void notifyUsers()
    {
        List<NotificationLogEntity> v_listaEnviar = logRepository.findByFoiEnviadoFalseAndTemErroFalse();
        for (NotificationLogEntity notificationLogEntity : v_listaEnviar)
        {
            try
            {
                this.sendSimpleEmail(notificationLogEntity.getRecipient(), notificationLogEntity.getSubject(), notificationLogEntity.getMessage());
                notificationLogEntity.setFoiEnviado(true);
                notificationLogEntity.setTemErro(false);
                notificationLogEntity.setDataEnvio(new Date());
            }
            catch (Exception e)
            {
                notificationLogEntity.setFoiEnviado(false);
                notificationLogEntity.setTemErro(true);
                notificationLogEntity.setErrorMessage(e.getMessage());

            }
            finally
            {
                logRepository.save(notificationLogEntity);
            }
        }
    }

    public void sendSimpleEmail(String to, String subject, String text)
    {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("seu.email@gmail.com");

        mailSender.send(message);
    }

}
