package com.merendabot.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public interface DatabaseConnection {

    Connection getConnection();

    String getUrl();

    Properties getProperties();

    PreparedStatement prepareStatement(String sql) throws SQLException;
}
