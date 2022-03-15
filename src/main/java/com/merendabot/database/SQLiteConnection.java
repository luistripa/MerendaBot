package com.merendabot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class SQLiteConnection implements DatabaseConnection {

    public static final String URL_PREFIX = "jdbc:sqlite://localhost/";

    private Connection connection;
    private String url;
    private Properties properties;

    public SQLiteConnection(String url, Properties properties) throws SQLException {
        this.url = url;
        this.properties = properties;
        this.connection = DriverManager.getConnection(URL_PREFIX+url, properties);
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }
}
