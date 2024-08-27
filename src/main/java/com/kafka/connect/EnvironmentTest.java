package com.kafka.connect;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentTest implements CommandLineRunner
{

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) throws Exception
    {
        // testando carregamento dos parametros
        // String kafkaConnectUrl = environment.getProperty("kafka.connect.url");
        // System.out.println("Kafka Connect URL: " + kafkaConnectUrl);
    }
}
