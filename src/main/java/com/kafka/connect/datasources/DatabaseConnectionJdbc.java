package com.kafka.connect.datasources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.kafka.connect.dto.DataAnaliseYearDTO;
import com.kafka.connect.entity.ConnectorConfigEntity;

public class DatabaseConnectionJdbc
{
    private Connection conn;

    private Statement stmt = null;

    private ResultSet rs = null;

    public DatabaseConnectionJdbc(ConnectorConfigEntity connector)
    {
        String url = "jdbc:postgresql://" + connector.getHost() + ":" + connector.getPort() + "/" + connector.getDatabase() + "";
        String user = connector.getUsuario();
        String password = connector.getSenha();

        try
        {
            conn = DriverManager.getConnection(url, user, password);

        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int getTotalRows(String p_query, String p_colunaTotal)
    {
        int total = 0;

        try
        {

            stmt = conn.createStatement();

            rs = stmt.executeQuery(p_query);

            // Processar o resultado
            while (rs.next())
            {

                total = rs.getInt(p_colunaTotal);
                // String name = rs.getString("name");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return total;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYear(String p_tabela, String p_nomeCliente, String p_nomeColunaData)
    {

        String v_query = "SELECT " + //
            " EXTRACT(YEAR FROM " + p_nomeColunaData + ")::int AS year, " + //
            " EXTRACT(MONTH FROM " + p_nomeColunaData + " )::int AS month, " + //
            " COUNT(*)::int AS total_records " + //
            " FROM " + p_tabela + //
            " GROUP BY year, month " + //
            " ORDER BY year, month; ";

        List<DataAnaliseYearDTO> v_listaDadosYearMouth = new ArrayList<DataAnaliseYearDTO>();

        try
        {

            stmt = conn.createStatement();

            rs = stmt.executeQuery(v_query);

            // Processar o resultado
            while (rs.next())
            {
                DataAnaliseYearDTO v_dataAnalise = new DataAnaliseYearDTO();
                v_dataAnalise.setNomeTabela(p_tabela);
                v_dataAnalise.setClienteNome(p_nomeCliente);
                v_dataAnalise.setYear(rs.getInt("year"));
                v_dataAnalise.setMonth(rs.getInt("month"));
                v_dataAnalise.setTotalRecordsPostgres(rs.getInt("total_records"));

                v_listaDadosYearMouth.add(v_dataAnalise);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return v_listaDadosYearMouth;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYearMonth(String p_tabela, String p_nomeCliente, String p_nomeColunaData, int year, int month)
    {

        String v_query = "SELECT " + //
            " EXTRACT(DAY FROM " + p_nomeColunaData + ")::int AS day, " + //
            " COUNT(*)::int AS total_records " + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + ") = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + ") = " + month + //
            " GROUP BY day " + //
            " ORDER BY day; ";

        List<DataAnaliseYearDTO> v_listaDadosYearMouth = new ArrayList<DataAnaliseYearDTO>();

        try
        {

            stmt = conn.createStatement();

            rs = stmt.executeQuery(v_query);

            // Processar o resultado
            while (rs.next())
            {
                DataAnaliseYearDTO v_dataAnalise = new DataAnaliseYearDTO();
                v_dataAnalise.setNomeTabela(p_tabela);
                v_dataAnalise.setClienteNome(p_nomeCliente);
                v_dataAnalise.setYear(year);
                v_dataAnalise.setMonth(month);
                v_dataAnalise.setDay(rs.getInt("day"));
                v_dataAnalise.setTotalRecordsPostgres(rs.getInt("total_records"));

                v_listaDadosYearMouth.add(v_dataAnalise);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return v_listaDadosYearMouth;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYearMonthDay(String p_tabela, String p_nomeCliente, String p_nomeColunaData, int year, int month, int day)
    {

        String v_query = "SELECT " + //
            " EXTRACT(HOUR FROM " + p_nomeColunaData + ")::INT AS hour, " + //
            " COUNT(*)::int AS total_records " + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + ") = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + ") = " + month + //
            " AND EXTRACT(DAY FROM " + p_nomeColunaData + ") = " + day + //
            " GROUP BY hour " + //
            " ORDER BY hour; ";

        List<DataAnaliseYearDTO> v_listaDadosYearMouth = new ArrayList<DataAnaliseYearDTO>();

        try
        {

            stmt = conn.createStatement();

            rs = stmt.executeQuery(v_query);

            // Processar o resultado
            while (rs.next())
            {
                DataAnaliseYearDTO v_dataAnalise = new DataAnaliseYearDTO();
                v_dataAnalise.setNomeTabela(p_tabela);
                v_dataAnalise.setClienteNome(p_nomeCliente);
                v_dataAnalise.setYear(year);
                v_dataAnalise.setMonth(month);
                v_dataAnalise.setDay(day);
                v_dataAnalise.setHour(rs.getInt("hour"));
                v_dataAnalise.setTotalRecordsPostgres(rs.getInt("total_records"));

                v_listaDadosYearMouth.add(v_dataAnalise);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return v_listaDadosYearMouth;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYearMonthDayHour(String p_tabela, String p_nomeCliente, String p_nomeColunaData, int year, int month, int day,
        int hour)
    {

        String v_query = "SELECT " + //
            " EXTRACT(MINUTE FROM " + p_nomeColunaData + ")::INT AS minutes, " + //
            " COUNT(*)::int AS total_records " + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + ") = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + ") = " + month + //
            " AND EXTRACT(DAY FROM " + p_nomeColunaData + ") = " + day + //
            " AND EXTRACT(HOUR FROM " + p_nomeColunaData + ") = " + hour + //
            " GROUP BY minutes " + //
            " ORDER BY minutes; ";

        List<DataAnaliseYearDTO> v_listaDadosYearMouth = new ArrayList<DataAnaliseYearDTO>();

        try
        {

            stmt = conn.createStatement();

            rs = stmt.executeQuery(v_query);

            // Processar o resultado
            while (rs.next())
            {
                DataAnaliseYearDTO v_dataAnalise = new DataAnaliseYearDTO();
                v_dataAnalise.setNomeTabela(p_tabela);
                v_dataAnalise.setClienteNome(p_nomeCliente);
                v_dataAnalise.setYear(year);
                v_dataAnalise.setMonth(month);
                v_dataAnalise.setDay(day);
                v_dataAnalise.setHour(hour);
                v_dataAnalise.setMinutes(rs.getInt("minutes"));
                v_dataAnalise.setTotalRecordsPostgres(rs.getInt("total_records"));

                v_listaDadosYearMouth.add(v_dataAnalise);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return v_listaDadosYearMouth;
    }

    public List<DataAnaliseYearDTO> getDataAnaliseYearMonthDayHourMinutes(String p_tabela, String p_nomeCliente, String p_nomeColunaData, int year, int month,
        int day, int hour, int minute)
    {

        String v_query = "SELECT oid" + //
            " FROM " + p_tabela + //
            " WHERE EXTRACT(YEAR FROM " + p_nomeColunaData + " ) = " + year + //
            " AND EXTRACT(MONTH FROM " + p_nomeColunaData + " ) = " + month + //
            " AND EXTRACT(DAY FROM " + p_nomeColunaData + " ) = " + day + //
            " AND EXTRACT(HOUR FROM " + p_nomeColunaData + " ) = " + hour + //
            " AND EXTRACT(MINUTE FROM " + p_nomeColunaData + " ) = " + minute + //
            " ; ";

        List<DataAnaliseYearDTO> v_listaDadosYearMouth = new ArrayList<DataAnaliseYearDTO>();

        try
        {

            stmt = conn.createStatement();

            rs = stmt.executeQuery(v_query);

            // Processar o resultado
            while (rs.next())
            {
                DataAnaliseYearDTO v_dataAnalise = new DataAnaliseYearDTO();
                v_dataAnalise.setNomeTabela(p_tabela);
                v_dataAnalise.setClienteNome(p_nomeCliente);
                v_dataAnalise.setYear(year);
                v_dataAnalise.setMonth(month);
                v_dataAnalise.setDay(day);
                v_dataAnalise.setHour(hour);
                v_dataAnalise.setMinutes(minute);
                v_dataAnalise.setOid(rs.getLong("oid"));

                v_listaDadosYearMouth.add(v_dataAnalise);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return v_listaDadosYearMouth;
    }

    public void close()
    {

        // Fechar recursos
        try
        {
            if (rs != null)
            {
                rs.close();
            }
            if (stmt != null)
            {
                stmt.close();
            }
            if (conn != null)
            {
                conn.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public List<Long> getOids(String p_key, String p_tabela)
    {
        List<Long> v_listaLong = new ArrayList<Long>();

        if (p_tabela != null && p_tabela.isEmpty() == false)
        {

            try
            {
                String v_sql = "SELECT " + p_key + " FROM " + p_tabela;

                stmt = conn.createStatement();

                rs = stmt.executeQuery(v_sql);

                while (rs.next())
                {
                    long v_oid = rs.getLong(p_key);
                    if (v_listaLong.contains(v_oid) == false)
                    {
                        v_listaLong.add(v_oid);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return v_listaLong;

    }

}
