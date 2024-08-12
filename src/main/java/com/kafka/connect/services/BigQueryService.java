package com.kafka.connect.services;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

@Service
public class BigQueryService
{

    private final Map<String, BigQuery> bigQueryClients;

    @Autowired
    public BigQueryService(Map<String, BigQuery> bigQueryClients)
    {
        this.bigQueryClients = bigQueryClients;
    }

    public TableResult executeQuery(String filename, String query) throws InterruptedException
    {
        TableResult v_result = null;

        BigQuery bigQuery = bigQueryClients.get(filename);
        if (bigQuery == null)
        {
            throw new IllegalArgumentException("No BigQuery client available for provided credentials file");
        }

        try
        {
            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();
            v_result = bigQuery.query(queryConfig);
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return v_result;
    }
}
