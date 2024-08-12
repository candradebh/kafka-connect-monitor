package com.kafka.connect.datasources;

import java.util.UUID;
import java.util.function.Consumer;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

public class BigQueryConnection
{

    public void queryBigQuery()
    {
        // Configurar o cliente BigQuery
        BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();
        System.out.println("Conexão com BigQuery estabelecida!");

        // Configurar a consulta
        String query = "SELECT * FROM `your_project.your_dataset.your_table` LIMIT 10"; // Substitua pelos valores reais
        QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query).build();

        // Executar a consulta
        JobId jobId = JobId.of(UUID.randomUUID().toString()); // Gerar um ID de trabalho único
        Job job = bigquery.create(JobInfo.newBuilder(queryConfig).setJobId(jobId).build());

        try
        {
            // Aguardar a conclusão da consulta
            job = job.waitFor();
            if (job == null)
            {
                System.out.println("Job not found.");
                return;
            }
            else if (job.getStatus().getError() != null)
            {
                System.out.println("Error: " + job.getStatus().getError().toString());
                return;
            }

            // Processar o resultado
            TableResult result = job.getQueryResults();
            result.iterateAll().forEach(new Consumer<FieldValueList>()
            {
                public void accept(FieldValueList row)
                {
                    System.out.println(row.toString());
                }
            });
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}
