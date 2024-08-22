package com.kafka.connect.services;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import com.kafka.connect.entity.NotificationLogEntity;
import com.kafka.connect.entity.RecipientEntity;
import com.kafka.connect.repository.NotificationLogRepository;
import com.kafka.connect.repository.RecipientRepository;

/**
 * Serviço que cria as notificações na tabela e associa a volumetria notificada.
 */

@Service("NotificationService")
public class NotificationService implements SchedulableTask
{

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private NotificationLogRepository logRepository;

    @Autowired
    private RecipientRepository recipientRepository;

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
        // List<NotificationLogEntity> v_listaEnviar = logRepository.findByFoiEnviadoFalseAndTemErroFalse();
        List<NotificationLogEntity> v_listaEnviar = logRepository.findByFoiEnviadoFalse();

        for (NotificationLogEntity notificationLogEntity : v_listaEnviar)
        {
            try
            {
                this.sendHtmlEmail(this.getEmailsAsCommaSeparatedString(notificationLogEntity.getRecipient()), notificationLogEntity.getSubject(),
                    notificationLogEntity.getMessage());

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

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException
    {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // true indica que é html
        helper.setFrom(fromEmail);

        mailSender.send(message);
    }

    public String getEmailsAsCommaSeparatedString(String p_recipient)
    {
        if (p_recipient == null || p_recipient.isEmpty())
        {
            List<RecipientEntity> v_recipients = recipientRepository.findByIsActiveTrue();
            return v_recipients.stream().map(RecipientEntity::getEmail).collect(Collectors.joining(","));
        }
        return p_recipient;
    }
}
