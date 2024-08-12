package com.kafka.connect.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.VolumetryYearEntity;

public interface VolumetryYearRepository extends JpaRepository<VolumetryYearEntity, Long>
{

    Optional<VolumetryYearEntity> findByClienteNome(String clienteNome);

    @Query("SELECT c FROM VolumetryYearEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.mes = :mes")
    Optional<VolumetryYearEntity> findByClienteNomeAnoMes(@Param("clienteNome") String clienteNome, @Param("ano") int ano, @Param("mes") int mes);

}
