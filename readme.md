# Cadastro de conectores

1 - Todos os conectores devem possuir o parametro "nome.cliente" com seu respectivo nome do cliente. Ex: "nome.cliente": "vale"
2 - Adicone o arquivo de credenciais do cliente na pasta: /src/main/resources/credentials/vale.json
3 - Configure o sistema com o ip do kafka no arquivo application.properties em spring.kafka.bootstrap-servers e kafka.connect.url


# Executando
`mvn clean install`
`mvn spring-boot:run`




https://docs.confluent.io/platform/current/connect/monitoring.html