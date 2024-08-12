package com.kafka.connect.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.kafka.connect.dto.DataAnaliseYearDTO;

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

    public List<DataAnaliseYearDTO> getDataAnaliseYear(String filename, String p_tabela, String p_nomeCliente)
    {

        String v_query = "SELECT " + //
            " EXTRACT(YEAR FROM datacriacaoservidor) AS year, " + //
            " EXTRACT(MONTH FROM datacriacaoservidor) AS month," + //
            " COUNT(*) AS total_records " + //
            " FROM " + p_tabela + //
            " GROUP BY year, month " + //
            " ORDER BY year, month; ";

        List<DataAnaliseYearDTO> v_listaDataAnaliseYear = new ArrayList<DataAnaliseYearDTO>();

        TableResult v_result = null;

        BigQuery bigQuery = bigQueryClients.get(filename);
        if (bigQuery == null)
        {
            throw new IllegalArgumentException("No BigQuery client available for provided credentials file");
        }

        try
        {
            QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(v_query).build();
            v_result = bigQuery.query(queryConfig);

            if (v_result != null)
            {
                for (FieldValueList row : v_result.iterateAll())
                {
                    DataAnaliseYearDTO v_dataAnaliseYear = new DataAnaliseYearDTO();
                    v_dataAnaliseYear.setClienteNome(p_nomeCliente);
                    v_dataAnaliseYear.setYear(Integer.parseInt(row.get("year").getStringValue()));
                    v_dataAnaliseYear.setMonth(Integer.parseInt(row.get("month").getStringValue()));
                    v_dataAnaliseYear.setTotalRecordsBigquery(row.get("year").getLongValue());

                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return v_listaDataAnaliseYear;
    }
}
