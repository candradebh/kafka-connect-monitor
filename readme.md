# Cadastro de conectores

1 - Todos os conectores devem possuir o parametro "nome.cliente" com seu respectivo nome do cliente. Ex: "nome.cliente": "vale"
2 - Adicone o arquivo de credenciais do cliente na pasta: /src/main/resources/credentials/vale.json
3 - Configure o sistema com o ip do kafka no arquivo application.properties em spring.kafka.bootstrap-servers e kafka.connect.url


# Publicar no servidor
Essa aplicação vai rodar em uma maquina linux

## Gerando jar 
`mvn clean package`

## Copiar arquivos
1 - Copiar o jar `connect-0.0.1-SNAPSHOT.jar` para a pasta `/var/lib/kafka-monitor`
2 - Copiar o shell script `/scripts/kafkamonitor.sh` para mesma pasta do JAR `/var/lib/kafka-monitor` e torná-lo executável `chmod +x kafkamonitor.sh`
3 - Criar o servico do linux `sudo vi /etc/systemd/system/kafka-monitor.service` e copie o conteudo abaixo:

```
[Unit]
Description=Kafka monitor
Documentation=https://github.com/candradebh/kafka-connect-monitor
After=syslog.target
After=network.target

[Service]
ExecStart=/var/lib/kafka-monitor/kafkamonitor.sh start
ExecStop=/var/lib/kafka-monitor/kafkamonitor.sh stop
Restart=always

[Install]
WantedBy=multi-user.target
```

4 - Cadastre e recarregue os serviços:
`sudo systemctl daemon-reload`
`sudo systemctl start kafka-monitor`

5 - Habilite o servico para iniciar com o computador `sudo systemctl enable kafka-monitor`

6 - verificar os logs `sudo journalctl -u myapp` 





## Links interessantes
https://docs.confluent.io/platform/current/connect/monitoring.html