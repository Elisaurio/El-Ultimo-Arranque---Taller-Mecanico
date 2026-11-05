package com.ultimoarranque.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private Database() {}

    public static Connection getConnection() throws SQLException {
        String url = env("DB_URL", "jdbc:mysql://localhost:3306/taller_mecanico?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String user = env("DB_USER", "root");
        String password = env("DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, password);
    }

    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null ? defaultValue : value;
    }
}
