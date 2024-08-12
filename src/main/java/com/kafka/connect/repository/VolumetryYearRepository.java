package com.kafka.connect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.VolumetryYearEntity;

public interface VolumetryYearRepository extends JpaRepository<VolumetryYearEntity, Long>
{

    Optional<VolumetryYearEntity> findByClienteNome(String clienteNome);

    @Query("SELECT c FROM VolumetryYearEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela AND c.mes = :mes")
    Optional<VolumetryYearEntity> findByClienteNomeTabelaAnoMes(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes);

    @Query("SELECT c FROM VolumetryYearEntity c WHERE c.clienteNome = :clienteNome AND c.nomeTabela = :nomeTabela ORDER BY ano, mes ")
    List<VolumetryYearEntity> findByClienteNomeTabela(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela);

}
