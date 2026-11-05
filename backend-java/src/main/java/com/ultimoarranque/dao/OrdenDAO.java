package com.ultimoarranque.dao;

import com.ultimoarranque.util.ApiException;
import com.ultimoarranque.util.ConexionBD;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class OrdenDAO {
    private static final String BASE = "SELECT o.*, v.placa, v.tipo, m.nombre AS mecanico, c.nombre AS cliente " +
            "FROM ordenes_trabajo o JOIN vehiculos v ON v.id=o.vehiculo_id " +
            "JOIN clientes c ON c.id=v.cliente_id JOIN mecanicos m ON m.id=o.mecanico_id ";

    public List<Map<String,Object>> listar() throws SQLException {
        try(Connection c=ConexionBD.getConnection(); PreparedStatement s=c.prepareStatement(BASE+" ORDER BY o.id DESC"); ResultSet r=s.executeQuery()) {
            List<Map<String,Object>> lista=new ArrayList<>();
            while(r.next()) lista.add(fila(r));
            return lista;
        }
    }

    public Map<String,Object> detalle(long id) throws SQLException {
        try(Connection c=ConexionBD.getConnection()) {
            Map<String,Object> o=orden(c,id,false);
            o.put("servicios", elementos(c,"SELECT os.servicio_id AS id, s.descripcion, os.costo_unitario AS costo " +
                    "FROM orden_servicios os JOIN servicios s ON s.id=os.servicio_id WHERE os.orden_id=?",id));
            o.put("repuestos", elementos(c,"SELECT ore.repuesto_id AS id, r.codigo, r.nombre, ore.cantidad, ore.precio_unitario AS precio " +
                    "FROM orden_repuestos ore JOIN repuestos r ON r.id=ore.repuesto_id WHERE ore.orden_id=?",id));
            return o;
        }
    }

    public Map<String,Object> crear(long vehiculoId,long mecanicoId,String problema) throws SQLException {
        String sql="INSERT INTO ordenes_trabajo (vehiculo_id,mecanico_id,problema,estado) VALUES (?,?,?,'PENDIENTE')";
        long id;
        try(Connection c=ConexionBD.getConnection();PreparedStatement s=c.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            s.setLong(1,vehiculoId);s.setLong(2,mecanicoId);s.setString(3,problema);
            s.executeUpdate();
            try(ResultSet keys=s.getGeneratedKeys()){if(!keys.next()) throw new SQLException("No se creó la orden");id=keys.getLong(1);}
        }
        return detalle(id);
    }

    public void iniciar(long id) throws SQLException {
        try(Connection c=ConexionBD.getConnection();PreparedStatement s=c.prepareStatement(
                "UPDATE ordenes_trabajo SET estado='EN_PROCESO' WHERE id=? AND estado='PENDIENTE'")) {
            s.setLong(1,id);
            if(s.executeUpdate()==0) throw new ApiException(409,"Solo puede iniciarse una orden PENDIENTE");
        }
    }

    public void agregarServicio(long id,long servicioId) throws SQLException {
        transaccion(c->{
            exigirEnProceso(c,id);
            BigDecimal costo=monto(c,"SELECT costo FROM servicios WHERE id=?",servicioId);
            try(PreparedStatement s=c.prepareStatement("INSERT INTO orden_servicios (orden_id,servicio_id,costo_unitario) VALUES (?,?,?)")) {
                s.setLong(1,id);s.setLong(2,servicioId);s.setBigDecimal(3,costo);s.executeUpdate();
            }
        });
    }

    public void quitarServicio(long id,long servicioId) throws SQLException {
        transaccion(c->{
            exigirEnProceso(c,id);
            try(PreparedStatement s=c.prepareStatement("DELETE FROM orden_servicios WHERE orden_id=? AND servicio_id=?")) {
                s.setLong(1,id);s.setLong(2,servicioId);
                if(s.executeUpdate()==0) throw new ApiException(404,"Servicio no asignado a esta orden");
            }
        });
    }

    public void agregarRepuesto(long id,long repuestoId,int cantidad) throws SQLException {
        transaccion(c->{
            exigirEnProceso(c,id);
            try(PreparedStatement s=c.prepareStatement("SELECT existencia,precio FROM repuestos WHERE id=? FOR UPDATE")) {
                s.setLong(1,repuestoId);
                try(ResultSet r=s.executeQuery()) {
                    if(!r.next()) throw new ApiException(404,"Repuesto no encontrado");
                    if(r.getInt("existencia")<cantidad) throw new ApiException(409,"No hay suficientes repuestos en inventario");
                    BigDecimal precio=r.getBigDecimal("precio");
                    try(PreparedStatement desc=c.prepareStatement("UPDATE repuestos SET existencia=existencia-? WHERE id=? AND existencia>=?")) {
                        desc.setInt(1,cantidad);desc.setLong(2,repuestoId);desc.setInt(3,cantidad);
                        if(desc.executeUpdate()==0) throw new ApiException(409,"Existencia insuficiente");
                    }
                    try(PreparedStatement ins=c.prepareStatement(
                      "INSERT INTO orden_repuestos (orden_id,repuesto_id,cantidad,precio_unitario) VALUES (?,?,?,?) " +
                      "ON DUPLICATE KEY UPDATE cantidad=cantidad+VALUES(cantidad)")) {
                        ins.setLong(1,id);ins.setLong(2,repuestoId);ins.setInt(3,cantidad);ins.setBigDecimal(4,precio);ins.executeUpdate();
                    }
                }
            }
        });
    }

    public void quitarRepuesto(long id,long repuestoId) throws SQLException {
        transaccion(c->{
            exigirEnProceso(c,id);
            int cantidad;
            try(PreparedStatement s=c.prepareStatement("SELECT cantidad FROM orden_repuestos WHERE orden_id=? AND repuesto_id=? FOR UPDATE")) {
                s.setLong(1,id);s.setLong(2,repuestoId);
                try(ResultSet r=s.executeQuery()) {
                    if(!r.next()) throw new ApiException(404,"Repuesto no asignado a esta orden");
                    cantidad=r.getInt(1);
                }
            }
            try(PreparedStatement s=c.prepareStatement("DELETE FROM orden_repuestos WHERE orden_id=? AND repuesto_id=?")) {
                s.setLong(1,id);s.setLong(2,repuestoId);s.executeUpdate();
            }
            devolverStock(c,repuestoId,cantidad);
        });
    }

    public void cancelar(long id) throws SQLException {
        transaccion(c->{
            String estado=bloquearEstado(c,id);
            if(!estado.equals("PENDIENTE") && !estado.equals("EN_PROCESO"))
                throw new ApiException(409,"Una orden finalizada o cancelada no se puede modificar");
            try(PreparedStatement s=c.prepareStatement("SELECT repuesto_id,cantidad FROM orden_repuestos WHERE orden_id=?")) {
                s.setLong(1,id);
                try(ResultSet r=s.executeQuery()) {
                    while(r.next()) devolverStock(c,r.getLong(1),r.getInt(2));
                }
            }
            try(PreparedStatement s=c.prepareStatement("UPDATE ordenes_trabajo SET estado='CANCELADA' WHERE id=?")) {
                s.setLong(1,id);s.executeUpdate();
            }
        });
    }

    public void finalizar(long id) throws SQLException {
        transaccion(c->{
            exigirEnProceso(c,id);
            int servicios=0;
            try(PreparedStatement s=c.prepareStatement("SELECT COUNT(*) FROM orden_servicios WHERE orden_id=?")) {
                s.setLong(1,id);
                try(ResultSet r=s.executeQuery()) { if(r.next()) servicios=r.getInt(1); }
            }
            if(servicios==0) throw new ApiException(409,"No se puede finalizar una orden sin servicios");
            BigDecimal costoServicios=total(c,"SELECT COALESCE(SUM(costo_unitario),0) FROM orden_servicios WHERE orden_id=?",id);
            BigDecimal costoRepuestos=total(c,"SELECT COALESCE(SUM(cantidad*precio_unitario),0) FROM orden_repuestos WHERE orden_id=?",id);
            try(PreparedStatement s=c.prepareStatement("UPDATE ordenes_trabajo SET estado='FINALIZADA', fecha_finalizacion=NOW(), total=? WHERE id=?")) {
                s.setBigDecimal(1,costoServicios.add(costoRepuestos));s.setLong(2,id);s.executeUpdate();
            }
        });
    }

    private BigDecimal total(Connection c,String sql,long id) throws SQLException {
        try(PreparedStatement s=c.prepareStatement(sql)) {
            s.setLong(1,id);
            try(ResultSet r=s.executeQuery()){return r.next()?r.getBigDecimal(1):BigDecimal.ZERO;}
        }
    }
    private BigDecimal monto(Connection c,String sql,long id) throws SQLException {
        try(PreparedStatement s=c.prepareStatement(sql)) {
            s.setLong(1,id);
            try(ResultSet r=s.executeQuery()) {
                if(!r.next()) throw new ApiException(404,"Servicio no encontrado");
                return r.getBigDecimal(1);
            }
        }
    }
    private void devolverStock(Connection c,long repuestoId,int cantidad) throws SQLException {
        try(PreparedStatement s=c.prepareStatement("UPDATE repuestos SET existencia=existencia+? WHERE id=?")) {
            s.setInt(1,cantidad);s.setLong(2,repuestoId);s.executeUpdate();
        }
    }
    private void exigirEnProceso(Connection c,long id) throws SQLException {
        if(!bloquearEstado(c,id).equals("EN_PROCESO"))
            throw new ApiException(409,"Solo se permite modificar órdenes EN_PROCESO");
    }
    private String bloquearEstado(Connection c,long id) throws SQLException {
        try(PreparedStatement s=c.prepareStatement("SELECT estado FROM ordenes_trabajo WHERE id=? FOR UPDATE")) {
            s.setLong(1,id);
            try(ResultSet r=s.executeQuery()) {
                if(!r.next()) throw new ApiException(404,"Orden no encontrada");
                return r.getString(1);
            }
        }
    }
    private Map<String,Object> orden(Connection c,long id,boolean bloqueo) throws SQLException {
        try(PreparedStatement s=c.prepareStatement(BASE+" WHERE o.id=?"+(bloqueo?" FOR UPDATE":""))) {
            s.setLong(1,id);
            try(ResultSet r=s.executeQuery()) {
                if(!r.next()) throw new ApiException(404,"Orden no encontrada");
                return fila(r);
            }
        }
    }
    private List<Map<String,Object>> elementos(Connection c,String sql,long id) throws SQLException {
        try(PreparedStatement s=c.prepareStatement(sql)) {
            s.setLong(1,id);
            try(ResultSet r=s.executeQuery()) {
                List<Map<String,Object>> lista=new ArrayList<>();
                while(r.next()) lista.add(fila(r));
                return lista;
            }
        }
    }
    private Map<String,Object> fila(ResultSet r) throws SQLException {
        ResultSetMetaData m=r.getMetaData();Map<String,Object> o=new LinkedHashMap<>();
        for(int i=1;i<=m.getColumnCount();i++) {
            Object valor=r.getObject(i);
            if(valor instanceof Timestamp t) valor=t.toLocalDateTime().toString();
            o.put(m.getColumnLabel(i),valor);
        }
        return o;
    }
    @FunctionalInterface private interface SQLAction { void run(Connection c) throws SQLException; }
    private void transaccion(SQLAction a) throws SQLException {
        try(Connection c=ConexionBD.getConnection()) {
            c.setAutoCommit(false);
            try { a.run(c); c.commit(); }
            catch(Exception ex) {
                c.rollback();
                if(ex instanceof SQLException sql) throw sql;
                if(ex instanceof RuntimeException re) throw re;
                throw new IllegalStateException(ex);
            }
        }
    }
}
