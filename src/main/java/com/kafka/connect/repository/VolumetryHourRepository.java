package com.kafka.connect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.VolumetryHourEntity;

public interface VolumetryHourRepository extends JpaRepository<VolumetryHourEntity, Long>
{

    Optional<VolumetryHourEntity> findByClienteNome(String clienteNome);

    @Query("SELECT c FROM VolumetryHourEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela AND c.mes = :mes AND c.dia = :dia AND c.hora = :hora AND c.minuto = :minuto")
    Optional<VolumetryHourEntity> findByClienteNomeTabelaAnoMesDiaHora(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes, @Param("dia") int dia, @Param("hora") int hora, @Param("minuto") int minuto);

    @Query("SELECT c FROM VolumetryHourEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela AND c.mes = :mes AND c.dia = :dia AND c.hora = :hora")
    List<VolumetryHourEntity> findByClienteNomeTabelaAnoMesDiaHora(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes, @Param("dia") int dia, @Param("hora") int hora);
}
