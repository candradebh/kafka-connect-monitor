# Cadastro de conectores

1 - Todos os conectores devem possuir o parametro "nome.cliente" com seu respectivo nome do cliente. Ex: "nome.cliente": "vale"
2 - Adicone o arquivo de credenciais do cliente na pasta: /src/main/resources/credentials/vale.json
3 - Configure o sistema com o ip do kafka no arquivo application.properties em spring.kafka.bootstrap-servers e kafka.connect.url


# Executando
`mvn clean install`
`mvn spring-boot:run`

## Gerando jar
`mvn clean package`




# Evolução
- Com a busca de volumetria por tabela por ano, mes, dia, hora e minutos para identificar erros de volumetria e apagá-los no banco de destino até que se ajuste esse problema, conseguimos identificar uma analise importante que pode ser desenvolvida, como a analise do comportamento dos dados, podemos identificar em qual momento do dia acontecem picos de INSERT no banco para uma tabela. (A IDEIA EH BOA MAS PESQUISAR POR DIA > HORA > MINUTOS EXIGE MUITO TEMPO PARA A ATUALIZAÇAO, FOQUE NO OBJETIVO E TRATE SEU TDAH)



## Links interessantes
https://docs.confluent.io/platform/current/connect/monitoring.html