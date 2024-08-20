package com.kafka.connect.datasources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;
import com.kafka.connect.dto.DataAnaliseYearDTO;

@Component
public class BigQueryConnection
{

    private final Map<String, BigQuery> bigQueryClients;

    @Autowired
    public BigQueryConnection(Map<String, BigQuery> bigQueryClients)
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
            e.printStackTrace();
        }

        return v_result;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYear(String filename, String p_tabela, String p_nomeCliente, String p_nomeColunaData)
    {

        String v_query = "SELECT " + //
            " EXTRACT(YEAR FROM " + p_nomeColunaData + ") AS year, " + //
            " EXTRACT(MONTH FROM " + p_nomeColunaData + ") AS month, " + //
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
                    v_dataAnaliseYear.setTotalRecordsBigquery(row.get("total_records").getLongValue());
                    v_dataAnaliseYear.setNomeTabelaBigquery(p_tabela);

                    v_listaDataAnaliseYear.add(v_dataAnaliseYear);
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return v_listaDataAnaliseYear;
    }

    /**
     * SELECT EXTRACT(DAY FROM datacriacaoservidor) AS day, COUNT(*) AS total_records FROM smartquestion.`_atendimento` WHERE EXTRACT(YEAR FROM
     * datacriacaoservidor) = 2023 AND EXTRACT(MONTH FROM datacriacaoservidor) = 5 GROUP BY day ORDER BY day;
     * 
     * @param filename
     * @param p_tabela
     * @param clienteNome
     * @param year
     * @param month
     * @return
     */
    public List<DataAnaliseYearDTO> getDataAnaliseYearMonth(String filename, String p_tabela, String p_nomeColunaData, String clienteNome, int year, int month)
    {

        String v_query = "SELECT " + //
            " EXTRACT(DAY FROM " + p_nomeColunaData + ") AS day, " + //
            " COUNT(*) AS total_records " + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + " ) = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + " ) = " + month + //
            " GROUP BY day " + //
            " ORDER BY day; ";

        List<DataAnaliseYearDTO> v_listaDataAnaliseYear = new ArrayList<DataAnaliseYearDTO>();

        TableResult v_result = null;

        BigQuery bigQuery = bigQueryClients.get(filename);
        if (bigQuery == null)
        {
            throw new IllegalArgumentException("Seu aquivo de credencial não é valido");
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
                    v_dataAnaliseYear.setClienteNome(clienteNome);
                    v_dataAnaliseYear.setYear(year);
                    v_dataAnaliseYear.setMonth(month);
                    v_dataAnaliseYear.setDay(Integer.parseInt(row.get("day").getStringValue()));
                    v_dataAnaliseYear.setTotalRecordsBigquery(row.get("total_records").getLongValue());
                    v_dataAnaliseYear.setNomeTabelaBigquery(p_tabela);

                    v_listaDataAnaliseYear.add(v_dataAnaliseYear);
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return v_listaDataAnaliseYear;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYearMonthDay(String filename, String p_tabela, String p_nomeColunaData, String clienteNome, int year,
        int month, int day)
    {

        String v_query = "SELECT " + //
            " EXTRACT(HOUR FROM  " + p_nomeColunaData + " ) AS hour, " + //
            " COUNT(*) AS total_records " + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + ") = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + ") = " + month + //
            " AND EXTRACT(DAY FROM " + p_nomeColunaData + " ) = " + day + //
            " GROUP BY hour " + //
            " ORDER BY hour; ";

        List<DataAnaliseYearDTO> v_listaDataAnaliseYear = new ArrayList<DataAnaliseYearDTO>();

        TableResult v_result = null;

        BigQuery bigQuery = bigQueryClients.get(filename);
        if (bigQuery == null)
        {
            throw new IllegalArgumentException("Seu aquivo de credencial não é valido");
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
                    v_dataAnaliseYear.setClienteNome(clienteNome);
                    v_dataAnaliseYear.setYear(year);
                    v_dataAnaliseYear.setMonth(month);
                    v_dataAnaliseYear.setDay(day);
                    v_dataAnaliseYear.setHour(Integer.parseInt(row.get("hour").getStringValue()));
                    v_dataAnaliseYear.setTotalRecordsBigquery(row.get("total_records").getLongValue());
                    v_dataAnaliseYear.setNomeTabelaBigquery(p_tabela);

                    v_listaDataAnaliseYear.add(v_dataAnaliseYear);
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return v_listaDataAnaliseYear;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYearMonthDayHour(String filename, String p_tabela, String p_nomeColunaData, String clienteNome, int year,
        int month, int day, int hour)
    {

        String v_query = "SELECT " + //
            " EXTRACT(MINUTE FROM " + p_nomeColunaData + ") AS minutes, " + //
            " COUNT(*) AS total_records " + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + ") = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + ") = " + month + //
            " AND EXTRACT(DAY FROM " + p_nomeColunaData + " ) = " + day + //
            " AND EXTRACT(HOUR FROM " + p_nomeColunaData + ") = " + hour + //
            " GROUP BY minutes " + //
            " ORDER BY minutes; ";

        List<DataAnaliseYearDTO> v_listaDataAnaliseYear = new ArrayList<DataAnaliseYearDTO>();

        TableResult v_result = null;

        BigQuery bigQuery = bigQueryClients.get(filename);
        if (bigQuery == null)
        {
            throw new IllegalArgumentException("Seu aquivo de credencial não é valido");
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
                    v_dataAnaliseYear.setClienteNome(clienteNome);
                    v_dataAnaliseYear.setYear(year);
                    v_dataAnaliseYear.setMonth(month);
                    v_dataAnaliseYear.setDay(day);
                    v_dataAnaliseYear.setHour(hour);
                    v_dataAnaliseYear.setMinutes(Integer.parseInt(row.get("minutes").getStringValue()));
                    v_dataAnaliseYear.setTotalRecordsBigquery(row.get("total_records").getLongValue());
                    v_dataAnaliseYear.setNomeTabelaBigquery(p_tabela);

                    v_listaDataAnaliseYear.add(v_dataAnaliseYear);
                }
            }
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }

        return v_listaDataAnaliseYear;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYearMonthDayHourMinutes(String filename, String p_tabela, String p_nomeColunaData, String clienteNome,
        int year, int month, int day, int hour, int minute)
    {

        String v_query = "SELECT " + //
            " oid " + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + " ) = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + " ) = " + month + //
            " AND EXTRACT(DAY FROM " + p_nomeColunaData + " ) = " + day + //
            " AND EXTRACT(HOUR FROM " + p_nomeColunaData + " ) = " + hour + //
            " AND EXTRACT(minute FROM " + p_nomeColunaData + " ) = " + minute + //
            " ; ";

        List<DataAnaliseYearDTO> v_listaDataAnaliseYear = new ArrayList<DataAnaliseYearDTO>();

        TableResult v_result = null;

        BigQuery bigQuery = bigQueryClients.get(filename);
        if (bigQuery == null)
        {
            throw new IllegalArgumentException("Seu aquivo de credencial não é valido");
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
                    v_dataAnaliseYear.setClienteNome(clienteNome);
                    v_dataAnaliseYear.setYear(year);
                    v_dataAnaliseYear.setMonth(month);
                    v_dataAnaliseYear.setDay(day);
                    v_dataAnaliseYear.setHour(hour);
                    v_dataAnaliseYear.setMinutes(minute);
                    v_dataAnaliseYear.setOid(Long.parseLong(row.get("oid").getStringValue()));
                    v_dataAnaliseYear.setNomeTabelaBigquery(p_tabela);

                    v_listaDataAnaliseYear.add(v_dataAnaliseYear);
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
