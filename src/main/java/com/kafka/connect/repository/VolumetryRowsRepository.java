package com.kafka.connect.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.kafka.connect.entity.VolumetryRowsEntity;

public interface VolumetryRowsRepository extends JpaRepository<VolumetryRowsEntity, Long>
{

    Optional<VolumetryRowsEntity> findByClienteNome(String clienteNome);

    @Query("SELECT c FROM VolumetryRowsEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela " //
        + "AND c.mes = :mes AND c.dia = :dia AND c.hora = :hora AND c.minuto = :minuto AND c.oid = :oid ")
    Optional<VolumetryRowsEntity> findByClienteNomeTabelaAnoMesDiaHora(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes, @Param("dia") int dia, @Param("hora") int hora, @Param("minuto") int minuto, @Param("oid") long oid);

    @Query("SELECT c FROM VolumetryRowsEntity c WHERE c.clienteNome = :clienteNome AND c.ano = :ano AND c.nomeTabela = :nomeTabela " //
        + "AND c.mes = :mes AND c.dia = :dia AND c.hora = :hora AND c.minuto = :minuto")
    List<VolumetryRowsEntity> findByClienteNomeTabelaAnoMesDiaHora(@Param("clienteNome") String clienteNome, @Param("nomeTabela") String nomeTabela,
        @Param("ano") int ano, @Param("mes") int mes, @Param("dia") int dia, @Param("hora") int hora, @Param("minuto") int minuto);

    @Query("SELECT c FROM VolumetryRowsEntity c WHERE c.deletado = false and c.postgres = false and c.bigquery = true")
    List<VolumetryRowsEntity> findByDeletadoIsFalse();

}
