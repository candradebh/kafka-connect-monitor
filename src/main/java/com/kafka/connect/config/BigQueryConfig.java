package com.kafka.connect.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;

@Configuration
public class BigQueryConfig
{

    @Bean
    public Map<String, BigQuery> bigQueryClients() throws IOException
    {
        Map<String, BigQuery> clients = new HashMap<String, BigQuery>();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:credentials/*.json");

        for (Resource resource : resources)
        {
            GoogleCredentials credentials = GoogleCredentials.fromStream(resource.getInputStream());
            BigQuery bigQuery = BigQueryOptions.newBuilder().setCredentials(credentials).build().getService();
            clients.put(resource.getFilename(), bigQuery);
        }

        return clients;
    }
}
