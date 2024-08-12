package com.kafka.connect.datasources;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

}
