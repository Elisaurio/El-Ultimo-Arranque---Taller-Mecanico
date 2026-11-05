package com.ultimoarranque.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class ConexionBD {
    private ConexionBD() {}

    public static Connection getConnection() throws SQLException {
        String url = variable("DB_URL", "jdbc:mysql://localhost:3306/taller_mecanico?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        String usuario = variable("DB_USER", "root");
        String contrasena = variable("DB_PASSWORD", "");
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private static String variable(String nombre, String predeterminado) {
        String valor = System.getenv(nombre);
        return valor == null ? predeterminado : valor;
    }

    public static void main(String[] args) {
        System.out.println("Probando conexion JDBC con MySQL...");
        try (Connection conexion = getConnection();
             PreparedStatement consulta = conexion.prepareStatement("SELECT 1");
             ResultSet resultado = consulta.executeQuery()) {
            if (resultado.next() && resultado.getInt(1) == 1) {
                System.out.println("CONEXION EXITOSA a la base: " + conexion.getCatalog());
            } else {
                throw new IllegalStateException("MySQL no devolvio el resultado esperado.");
            }
        } catch (SQLException e) {
            System.err.println("ERROR AL CONECTAR: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState() + " | Codigo: " + e.getErrorCode());
            throw new IllegalStateException("Revisa MySQL, las credenciales y DB_URL.", e);
        }
    }
}
