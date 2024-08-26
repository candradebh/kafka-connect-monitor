# Cadastro de conectores

1 - Todos os conectores devem possuir o parametro "nome.cliente" com seu respectivo nome do cliente. Ex: "nome.cliente": "vale" <br>
2 - Adicone o arquivo de credenciais do cliente na pasta: /src/main/resources/credentials/vale.json <br>
3 - Configure o sistema com o ip do kafka no arquivo application.properties em "spring.kafka.bootstrap-servers" e "kafka.connect.url" <br>


# Publicar no servidor
Essa aplicação vai rodar em uma maquina linux

## Gerando jar 
`mvn clean package`

## Copiar arquivos
1 - Copiar o jar `monitor-kafka.jar` para a pasta `/var/lib/kafka-monitor` <br>
2 - Criar o servico do linux `sudo vi /etc/systemd/system/kafka-monitor.service` e copie o conteudo abaixo: <br>

```
[Unit]
Description=Kafka monitor
Documentation=https://github.com/candradebh/kafka-connect-monitor
After=network.target

[Service]
Type=simple
ExecStart=/usr/bin/java -Djava.util.logging.config.file=src/main/resources/logging.properties -jar /var/lib/kafka-monitor/monitor-kafka.jar
StandardOutput=file:/var/lib/kafka-monitor/logs/kafka-monitor.log
StandardError=file:/var/lib/kafka-monitor/logs/kafka-monitor-error.log
Restart=on-failure

[Install]
WantedBy=multi-user.target
```

4 - Cadastre e recarregue os serviços:<br>
`sudo systemctl daemon-reload` <br>
`sudo systemctl start kafka-monitor` <br>

5 - Habilite o servico para iniciar com o computador `sudo systemctl enable kafka-monitor`<br>

6 - verificar os logs `sudo journalctl -u kafka-monitor -f` <br> 





## Links interessantes
https://docs.confluent.io/platform/current/connect/monitoring.html