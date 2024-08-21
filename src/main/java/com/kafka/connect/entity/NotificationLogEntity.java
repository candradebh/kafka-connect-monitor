package com.kafka.connect.entity;

import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_log")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationLogEntity
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean foiEnviado;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean temErro;

    private String nomeCliente;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String errorMessage; // Para armazenar mensagens de erro

    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = true)
    private Date dataEnvio;

    @Column(nullable = true)
    private Date dataModificacao;

    @PrePersist
    protected void onCreate()
    {
        dataCriacao = LocalDateTime.now();
    }
}
