package com.kafka.connect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.VolumetryDayEntity;

public interface VolumetryDayRepository extends JpaRepository<VolumetryDayEntity, Long>
{

    Optional<VolumetryDayEntity> findByClienteNome(String clienteNome);

    @Query("SELECT c FROM VolumetryDayEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela AND c.mes = :mes AND c.dia = :dia AND c.hora = :hora")
    Optional<VolumetryDayEntity> findByClienteNomeTabelaAnoMesDiaHora(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes, @Param("dia") int dia, @Param("hora") int hora);

    @Query("SELECT c FROM VolumetryDayEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela AND c.mes = :mes AND c.dia = :dia")
    List<VolumetryDayEntity> findByClienteNomeTabelaAnoMesDiaHora(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes, @Param("dia") int dia);
}
