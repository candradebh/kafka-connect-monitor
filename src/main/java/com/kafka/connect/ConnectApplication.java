package com.kafka.connect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConnectApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ConnectApplication.class, args);
    }
}
