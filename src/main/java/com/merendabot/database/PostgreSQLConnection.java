package com.merendabot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class PostgreSQLConnection implements DatabaseConnection {

    public static final String URL_PREFIX = "jdbc:postgresql://localhost/";

    private Connection connection;
    private String url;
    private Properties properties;

    public PostgreSQLConnection(String url, Properties properties) throws SQLException {
        this.url = url;
        this.properties = properties;
        this.connection = DriverManager.getConnection(URL_PREFIX+url, properties);
    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public Properties getProperties() {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
