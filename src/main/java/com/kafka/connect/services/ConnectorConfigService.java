package com.kafka.connect.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kafka.connect.dto.ConnectorSummaryDTO;
import com.kafka.connect.entity.ConnectorConfigEntity;
import com.kafka.connect.entity.ConnectorStatusEntity;
import com.kafka.connect.entity.NotificationLogEntity;
import com.kafka.connect.entity.RecipientEntity;
import com.kafka.connect.entity.TaskStatusEntity;
import com.kafka.connect.model.ConnectorConfig;
import com.kafka.connect.model.ConnectorStatus;
import com.kafka.connect.model.ConnectorStatus.Task;
import com.kafka.connect.repository.ConnectorConfigRepository;
import com.kafka.connect.repository.ConnectorStatusRepository;
import com.kafka.connect.repository.NotificationLogRepository;
import com.kafka.connect.repository.RecipientRepository;

@Service
public class ConnectorConfigService
{

    private static final Logger logger = Logger.getLogger(KafkaConnectorStatusService.class.getName());

    @Autowired
    private ConnectorConfigRepository repository;

    @Autowired
    private ConnectorStatusRepository connectorStatusRepository;

    @Autowired
    private NotificationLogRepository logRepository;

    @Autowired
    private RecipientRepository recipientRepository;

    @Autowired
    private KafkaConnectService kafkaConnectService;

    @PostConstruct
    public void init()
    {
        this.getAndSaveConnectorConfig();
    }

    public List<ConnectorConfigEntity> findAll()
    {
        return repository.findAll();
    }

    public Optional<ConnectorConfigEntity> getConnectorDetails(Long id)
    {
        return repository.findById(id);
    }

    public List<ConnectorConfigEntity> getConnectorForCliente(String nome)
    {
        return repository.findByNomeCliente(nome);
    }

    public Optional<ConnectorConfigEntity> findByTypeAndNomeCliente(String type, String nomeCliente)
    {
        return repository.findByTypeAndNomeCliente(type, nomeCliente);
    }

    public List<ConnectorSummaryDTO> getConnectorsGroupedByClientName()
    {
        return repository.findAllGroupedByClientName();
    }

    /**
     * Busca as configurações do conector no kafka e atualiza na base de dados dessa aplicação
     * 
     * @param connector
     */
    void getAndSaveConnectorConfig()
    {
        logger.info("Inicio - Atualizando conectores");
        StringBuffer v_mensagemErro = new StringBuffer();
        String v_assunto = "Erro ";
        try
        {
            // busca os conectores cadastrados no kafka
            List<String> connectors = kafkaConnectService.getConnectors();

            for (String v_connector : connectors)
            {
                ConnectorConfigEntity connectorConfig;

                Optional<ConnectorConfigEntity> optionalConnector = repository.findByName(v_connector);

                if (optionalConnector.isPresent())
                {
                    connectorConfig = optionalConnector.get();
                }
                else
                {
                    connectorConfig = new ConnectorConfigEntity();

                }

                connectorConfig.setName(v_connector);

                connectorConfig = repository.save(connectorConfig);

                if (connectorConfig != null)
                {
                    // logger.info("Inicio - Atualizando status dos conectores");
                    // o tipo dele source ou sink é obtido na chamada api de status
                    ConnectorStatus status = kafkaConnectService.getConnectorStatus(connectorConfig.getName());
                    connectorConfig.setType(status.getType()); // Defina o tipo conforme necessário

                    ConnectorStatusEntity statusEntity = new ConnectorStatusEntity();
                    statusEntity.setDataBusca(new Date());
                    statusEntity.setConnector(connectorConfig);
                    statusEntity.setConnectorState(status.getConnector().getState());
                    statusEntity.setConnectorWorkerId(status.getConnector().getWorker_id());

                    // Processar e salvar o status das tarefas
                    List<TaskStatusEntity> taskEntities = new ArrayList<TaskStatusEntity>();
                    for (Task task : status.getTasks())
                    {
                        TaskStatusEntity taskEntity = new TaskStatusEntity();
                        taskEntity.setTaskId(task.getId());
                        taskEntity.setTaskState(task.getState());
                        taskEntity.setTaskWorkerId(task.getWorker_id());
                        taskEntities.add(taskEntity);
                    }

                    statusEntity.setTasks(taskEntities);

                    // Verificar erros e salvar motivo
                    if (!"RUNNING".equalsIgnoreCase(status.getConnector().getState()))
                    {
                        statusEntity.setErrorReason("Conector não está em estado RUNNING");

                        v_assunto += " com status da do conector " + connectorConfig.getName();
                        v_mensagemErro.append("Conector: " + connectorConfig.getName());
                        v_mensagemErro.append("Status Conector: " + status.getConnector().getState());
                        v_mensagemErro.append("Erro: " + statusEntity.getErrorReason());

                    }
                    String v_statusTask = "";
                    for (ConnectorStatus.Task task : status.getTasks())
                    {
                        v_statusTask += task.getState() + " ";

                        if (!"RUNNING".equalsIgnoreCase(task.getState()))
                        {
                            statusEntity.setErrorReason("Tarefa " + task.getId() + " do conector não está em estado RUNNING");

                            v_assunto += " com status da task do conector " + connectorConfig.getName();
                            v_mensagemErro.append("Conector: " + connectorConfig.getName());
                            v_mensagemErro.append("Status Task: " + task.getState());
                            v_mensagemErro.append("Erro: " + statusEntity.getErrorReason());
                        }
                    }

                    connectorStatusRepository.save(statusEntity);

                    // SALVAR O ULTIMO STATUS CONECTOR
                    connectorConfig.setUltimoStatusConector(status.getConnector().getState());
                    connectorConfig.setUltimoStatusTask1(v_statusTask);
                    connectorConfig.setDataUltimoStatus(new Date());
                    // logger.info("Inicio - Atualizando as configurações dos conectores");

                    // o restante das configuracoes eh obtido na chamada de /config
                    ConnectorConfig v_configServer = kafkaConnectService.getConnectorConfig(connectorConfig.getName());
                    connectorConfig.setNomeCliente(v_configServer.getNomeCliente());
                    connectorConfig.setHost(v_configServer.getHostname());
                    connectorConfig.setPort(v_configServer.getPort());
                    connectorConfig.setUsuario(v_configServer.getUser());
                    connectorConfig.setSenha(v_configServer.getPassword());
                    connectorConfig.setClassname(v_configServer.getClassname());
                    connectorConfig.setDatabase(v_configServer.getDbname());
                    connectorConfig.setTableIncludeList(v_configServer.getTableIncludeList());
                    connectorConfig.setDefaultDataset(v_configServer.getDefaultDataset());
                    connectorConfig.setProjectBigquery(v_configServer.getProjectBigquery());
                    connectorConfig.setMergeIntervalMs(v_configServer.getMergeIntervalMs());

                    connectorConfig = repository.save(connectorConfig);

                    this.createNotification(connectorConfig.getNomeCliente(), v_assunto, v_mensagemErro.toString());

                    // logger.info("Conector: " + connectorConfig.getName() + " - Atualizado com sucesso! ");
                }
            }

            // logger.info("Deletando os conectores que não existem no kafka");

            // deleta os conectors que não existe no kafka
            this.checkAndDeleteByNames(connectors);
        }
        catch (Exception e)
        {
            v_mensagemErro.append("Não foi possivel obter o status, configurações dos conectores do Kafka." + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            v_assunto += " kafka ";
            this.createNotification("kafka", v_assunto, v_mensagemErro.toString());

        }

        logger.info("Fim da atualização dos conectores");

    }

    @Transactional
    public void checkAndDeleteByNames(List<String> connectors)
    {
        List<ConnectorConfigEntity> v_listaDeletar = new ArrayList<ConnectorConfigEntity>();
        List<ConnectorConfigEntity> v_listaConnectoresCadastrados = repository.findAll();
        for (ConnectorConfigEntity connectorConfigEntity : v_listaConnectoresCadastrados)
        {
            if (connectors.contains(connectorConfigEntity.getName()) == false)
            {
                v_listaDeletar.add(connectorConfigEntity);
            }
        }

        logger.info("Conectores para deletar: " + v_listaDeletar.toString());

        repository.deleteAll(v_listaDeletar);

        /*List<String> namesToDelete = v_listaDeletar.stream().map(ConnectorConfigEntity::getName).toList();
        
        if (!namesToDelete.isEmpty())
        {
            repository.deleteAllByNameIn(namesToDelete);
        }*/
    }

    private void createNotification(String p_nomeCliente, String p_assunto, String p_mensagem)
    {
        if (p_mensagem != null && p_mensagem.length() > 0)
        {

            // criando a notificacao
            NotificationLogEntity v_notificationLog = new NotificationLogEntity();
            v_notificationLog.setRecipient(this.getEmailsAsCommaSeparatedString());
            v_notificationLog.setSubject(p_assunto);
            v_notificationLog.setMessage(p_mensagem);
            v_notificationLog.setNomeCliente(p_nomeCliente);

            logRepository.save(v_notificationLog);
        }
    }

    public String getEmailsAsCommaSeparatedString()
    {
        List<RecipientEntity> v_recipients = recipientRepository.findByIsActiveTrue();
        return v_recipients.stream().map(RecipientEntity::getEmail).collect(Collectors.joining(","));
    }

}
