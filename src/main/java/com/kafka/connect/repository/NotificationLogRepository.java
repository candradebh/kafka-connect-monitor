package com.kafka.connect.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.NotificationLogEntity;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, Long>
{

    List<NotificationLogEntity> findByFoiEnviadoFalseAndTemErroFalse();

    List<NotificationLogEntity> findByFoiEnviadoFalse();

    List<NotificationLogEntity> findByTemErroTrue();

    @Override
    Page<NotificationLogEntity> findAll(Pageable pageable);

    @Query("SELECT log FROM NotificationLogEntity log " //
        + "WHERE (:nomeCliente IS NULL OR log.nomeCliente = :nomeCliente) " //
        + "AND (:recipient IS NULL OR log.recipient LIKE %:recipient%) " //
        + "AND (:subject IS NULL OR log.subject LIKE %:subject%) " //
    // + "AND (:startDate IS NULL OR log.dataCriacao >= :startDate) " //
    // + "AND (:endDate IS NULL OR log.dataCriacao <= :endDate)"
    )
    Page<NotificationLogEntity> findFilteredLogs(@Param("nomeCliente") String nomeCliente, @Param("recipient") String recipient,
        @Param("subject") String subject, Pageable pageable);
}
