package com.ultimoarranque.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {
    private ConexionBD() {}

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Falta el controlador JDBC de MySQL.", ex);
        }
        String url = System.getenv().getOrDefault("DB_URL",
                "jdbc:mysql://localhost:3306/taller_mecanico?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String usuario = System.getenv().getOrDefault("DB_USER", "root");
        String password = System.getenv("DB_PASSWORD");
        if (password == null) {
            throw new SQLException("Define la variable DB_PASSWORD en la terminal antes de iniciar Maven.");
        }
        return DriverManager.getConnection(url, usuario, password);
    }
}
