package com.kafka.connect.controller;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.kafka.connect.datasources.BigQueryConnection;
import com.kafka.connect.entity.ConnectorVolumetryEntity;
import com.kafka.connect.entity.VolumetryDayEntity;
import com.kafka.connect.entity.VolumetryHourEntity;
import com.kafka.connect.entity.VolumetryMonthDayEntity;
import com.kafka.connect.entity.VolumetryRowsEntity;
import com.kafka.connect.entity.VolumetryYearEntity;
import com.kafka.connect.repository.VolumetryDayRepository;
import com.kafka.connect.repository.VolumetryHourRepository;
import com.kafka.connect.repository.VolumetryMonthRepository;
import com.kafka.connect.repository.VolumetryRowsRepository;
import com.kafka.connect.repository.VolumetryYearRepository;
import com.kafka.connect.services.ConnectorVolumetryService;

@RestController
@RequestMapping("/volumetries")
public class ConnectorVolumetryController
{

    @Autowired
    private ConnectorVolumetryService service;

    @Autowired
    private VolumetryYearRepository volumetryYearRepository;

    @Autowired
    private VolumetryMonthRepository volumetryMonthRepository;

    @Autowired
    private VolumetryDayRepository volumetryDayRepository;

    @Autowired
    VolumetryHourRepository volumetryHourRepository;

    @Autowired
    VolumetryRowsRepository volumetryRowsRepository;

    @Autowired
    private BigQueryConnection bigqueryService;

    @GetMapping
    public ResponseEntity<List<ConnectorVolumetryEntity>> getAllVolumetries()
    {
        List<ConnectorVolumetryEntity> volumetries = service.findAll();
        return ResponseEntity.ok(volumetries);
    }

    @GetMapping("/{nomeCliente}")
    public ResponseEntity<List<ConnectorVolumetryEntity>> getConnectorDetails(@PathVariable("nomeCliente") String nomeCliente)
    {
        List<ConnectorVolumetryEntity> volumetries = service.getVolumetry(nomeCliente);

        return ResponseEntity.ok(volumetries);

    }

    @GetMapping("/{nomeCliente}/{nomeTabela}")
    public ResponseEntity<List<VolumetryYearEntity>> getConnectorDetails(@PathVariable("nomeCliente") String nomeCliente,
        @PathVariable("nomeTabela") String nomeTabela)
    {
        List<VolumetryYearEntity> volumetries = volumetryYearRepository.findByClienteNomeTabela(nomeCliente, nomeTabela);

        return ResponseEntity.ok(volumetries);

    }

    @GetMapping("/{nomeCliente}/{nomeTabela}/{ano}/{mes}")
    public ResponseEntity<List<VolumetryMonthDayEntity>> getConnectorDetailsTableAnoMes(@PathVariable("nomeCliente") String nomeCliente,
        @PathVariable("nomeTabela") String nomeTabela, @PathVariable("ano") int ano, @PathVariable("mes") int mes)
    {
        List<VolumetryMonthDayEntity> volumetries = volumetryMonthRepository.findByClienteNomeTabelaAnoMesDia(nomeCliente, nomeTabela, ano, mes);

        return ResponseEntity.ok(volumetries);

    }

    @GetMapping("/{nomeCliente}/{nomeTabela}/{ano}/{mes}/{dia}")
    public ResponseEntity<List<VolumetryDayEntity>> getConnectorDetailsTableAnoMesDia(@PathVariable("nomeCliente") String nomeCliente,
        @PathVariable("nomeTabela") String nomeTabela, @PathVariable("ano") int ano, @PathVariable("mes") int mes, @PathVariable("dia") int dia)
    {
        List<VolumetryDayEntity> volumetries = volumetryDayRepository.findByClienteNomeTabelaAnoMesDiaHora(nomeCliente, nomeTabela, ano, mes, dia);

        return ResponseEntity.ok(volumetries);

    }

    @GetMapping("/{nomeCliente}/{nomeTabela}/{ano}/{mes}/{dia}/{hora}")
    public ResponseEntity<List<VolumetryHourEntity>> getConnectorDetailsTableAnoMesDia(@PathVariable("nomeCliente") String nomeCliente,
        @PathVariable("nomeTabela") String nomeTabela, @PathVariable("ano") int ano, @PathVariable("mes") int mes, @PathVariable("dia") int dia,
        @PathVariable("hora") int hora)
    {
        List<VolumetryHourEntity> volumetries = volumetryHourRepository.findByClienteNomeTabelaAnoMesDiaHora(nomeCliente, nomeTabela, ano, mes, dia, hora);

        return ResponseEntity.ok(volumetries);

    }

    @GetMapping("/{nomeCliente}/{nomeTabela}/{ano}/{mes}/{dia}/{hora}/{minuto}")
    public ResponseEntity<List<VolumetryRowsEntity>> getConnectorDetailsTableAnoMesDiaHoraMinutos(@PathVariable("nomeCliente") String nomeCliente,
        @PathVariable("nomeTabela") String nomeTabela, @PathVariable("ano") int ano, @PathVariable("mes") int mes, @PathVariable("dia") int dia,
        @PathVariable("hora") int hora, @PathVariable("minuto") int minuto)
    {
        List<VolumetryRowsEntity> volumetries = volumetryRowsRepository.findByClienteNomeTabelaAnoMesDiaHora(nomeCliente, nomeTabela, ano, mes, dia, hora,
            minuto);

        return ResponseEntity.ok(volumetries);

    }

    @PostMapping("/deletar")
    public ResponseEntity<List<VolumetryRowsEntity>> processVolumetry(@RequestBody VolumetryRowsEntity volumetry)
    {
        // Deletar o objeto na bigquery
        try
        {

            String v_query = "DELETE FROM " + volumetry.getNomeTabelaBigquery() + " WHERE oid = " + volumetry.getOid();
            System.out.println(v_query);

            bigqueryService.executeQuery(volumetry.getClienteNome() + ".json", v_query);

            volumetry.setDataDeletado(new Date());
            volumetry.setDeletado(true);

            volumetryRowsRepository.save(volumetry);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        List<VolumetryRowsEntity> volumetries = volumetryRowsRepository.findByClienteNomeTabelaAnoMesDiaHora(volumetry.getClienteNome(),
            volumetry.getNomeTabela(), volumetry.getAno(), volumetry.getMes(), volumetry.getDia(), volumetry.getHora(), volumetry.getMinuto());

        return ResponseEntity.ok(volumetries);
    }
}
