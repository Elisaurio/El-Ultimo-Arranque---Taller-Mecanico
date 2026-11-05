package com.ultimoarranque.dao;

import com.ultimoarranque.util.ConexionBD;
import java.sql.*;
import java.util.*;

public class CatalogoDAO {
    private final String tabla;
    private final List<String> columnas;

    public CatalogoDAO(String modulo) {
        switch (modulo) {
            case "vehiculos" -> { tabla = "vehiculos"; columnas = List.of("cliente_id", "tipo", "placa", "marca", "modelo", "anio"); }
            case "mecanicos" -> { tabla = "mecanicos"; columnas = List.of("nombre", "especialidad", "telefono"); }
            case "servicios" -> { tabla = "servicios"; columnas = List.of("descripcion", "costo"); }
            case "repuestos" -> { tabla = "repuestos"; columnas = List.of("codigo", "nombre", "descripcion", "precio", "existencia"); }
            default -> throw new IllegalArgumentException("Catálogo no permitido");
        }
    }

    public List<Map<String,Object>> listar(Long clienteId) throws SQLException {
        String sql = "SELECT * FROM " + tabla + (clienteId != null && tabla.equals("vehiculos") ? " WHERE cliente_id = ?" : "") + " ORDER BY id DESC";
        List<Map<String,Object>> lista = new ArrayList<>();
        try (Connection c = ConexionBD.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            if (clienteId != null && tabla.equals("vehiculos")) s.setLong(1, clienteId);
            try (ResultSet r = s.executeQuery()) { while (r.next()) lista.add(fila(r)); }
        }
        return lista;
    }

    public Optional<Map<String,Object>> buscar(long id) throws SQLException {
        try (Connection c = ConexionBD.getConnection(); PreparedStatement s = c.prepareStatement("SELECT * FROM " + tabla + " WHERE id = ?")) {
            s.setLong(1, id);
            try (ResultSet r = s.executeQuery()) { return r.next() ? Optional.of(fila(r)) : Optional.empty(); }
        }
    }

    public Map<String,Object> crear(Map<String,Object> datos) throws SQLException {
        String nombres = String.join(",", columnas);
        String sql = "INSERT INTO " + tabla + " (" + nombres + ") VALUES (" + String.join(",", Collections.nCopies(columnas.size(), "?")) + ")";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement s = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            poner(s, datos, columnas);
            s.executeUpdate();
            try (ResultSet k = s.getGeneratedKeys()) { if (k.next()) return buscar(k.getLong(1)).orElseThrow(); }
        }
        throw new SQLException("No se pudo recuperar el registro creado");
    }

    public boolean actualizar(long id, Map<String,Object> datos) throws SQLException {
        List<String> actualizables = new ArrayList<>(columnas);
        if (tabla.equals("repuestos")) actualizables.remove("existencia");
        List<String> sets = new ArrayList<>();
        for (String columna : actualizables) sets.add(columna + " = ?");
        String sql = "UPDATE " + tabla + " SET " + String.join(",", sets) + " WHERE id = ?";
        try (Connection c = ConexionBD.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            poner(s, datos, actualizables);
            s.setLong(actualizables.size() + 1, id);
            return s.executeUpdate() > 0;
        }
    }

    public boolean eliminar(long id) throws SQLException {
        try (Connection c = ConexionBD.getConnection(); PreparedStatement s = c.prepareStatement("DELETE FROM " + tabla + " WHERE id = ?")) {
            s.setLong(1, id);
            return s.executeUpdate() > 0;
        }
    }


    public boolean abastecer(long id, int cantidad) throws SQLException {
        if (!tabla.equals("repuestos")) throw new IllegalArgumentException("Solo se abastecen repuestos");
        try (Connection c = ConexionBD.getConnection();
             PreparedStatement s = c.prepareStatement("UPDATE repuestos SET existencia=existencia+? WHERE id=?")) {
            s.setInt(1, cantidad);s.setLong(2,id);return s.executeUpdate()>0;
        }
    }

    private void poner(PreparedStatement s, Map<String,Object> datos, List<String> campos) throws SQLException {
        for (int i=0;i<campos.size();i++) {
            Object valor=datos.get(campos.get(i));
            if (valor == null) s.setNull(i+1, Types.NULL);
            else s.setObject(i+1, valor);
        }
    }

    private Map<String,Object> fila(ResultSet r) throws SQLException {
        Map<String,Object> datos = new LinkedHashMap<>();
        ResultSetMetaData m=r.getMetaData();
        for(int i=1;i<=m.getColumnCount();i++) datos.put(m.getColumnLabel(i), r.getObject(i));
        return datos;
    }
}
