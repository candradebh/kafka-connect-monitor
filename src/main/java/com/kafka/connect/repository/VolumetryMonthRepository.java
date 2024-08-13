package com.kafka.connect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.VolumetryMonthDayEntity;

public interface VolumetryMonthRepository extends JpaRepository<VolumetryMonthDayEntity, Long>
{

    Optional<VolumetryMonthDayEntity> findByClienteNome(String clienteNome);

    @Query("SELECT c FROM VolumetryMonthDayEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela AND c.mes = :mes AND c.dia = :dia")
    Optional<VolumetryMonthDayEntity> findByClienteNomeTabelaAnoMesDia(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes, @Param("dia") int dia);

    @Query("SELECT c FROM VolumetryMonthDayEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela AND c.mes = :mes")
    List<VolumetryMonthDayEntity> findByClienteNomeTabelaAnoMesDia(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes);
}
