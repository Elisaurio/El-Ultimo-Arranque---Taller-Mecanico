package com.ultimoarranque.report;

import com.ultimoarranque.dao.OrdenDAO;
import com.ultimoarranque.util.ApiException;
import com.ultimoarranque.util.ConexionBD;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class ReportesDAO {
    public byte[] reporte(String tipo, Map<String,String> filtros) throws Exception {
        String titulo;
        String sql;
        List<Object> parametros = new ArrayList<>();
        switch(tipo) {
            case "estados" -> {
                titulo="Ordenes por estado";
                sql="SELECT o.id AS Orden, v.placa AS Placa, m.nombre AS Mecanico, o.estado AS Estado, " +
                    "DATE_FORMAT(o.fecha_creacion,'%d/%m/%Y') AS Fecha FROM ordenes_trabajo o " +
                    "JOIN vehiculos v ON o.vehiculo_id=v.id JOIN mecanicos m ON o.mecanico_id=m.id ORDER BY o.estado,o.id";
            }
            case "vehiculo" -> {
                titulo="Historial por vehiculo";
                String placa=Objects.toString(filtros.get("placa"),"").trim();
                if(placa.isBlank()) throw new ApiException(400,"Ingresa una placa para generar el historial");
                sql="SELECT o.id AS Orden, v.placa AS Placa, o.problema AS Problema, o.estado AS Estado, o.total AS Quetzales " +
                    "FROM ordenes_trabajo o JOIN vehiculos v ON o.vehiculo_id=v.id WHERE v.placa=? ORDER BY o.id DESC";
                parametros.add(placa);
                titulo+=" - "+placa;
            }
            case "bajo-stock" -> {
                titulo="Repuestos con existencia <= 5";
                sql="SELECT codigo AS Codigo, nombre AS Repuesto, existencia AS Existencia, precio AS Precio_Q " +
                    "FROM repuestos WHERE existencia<=5 ORDER BY existencia,codigo";
            }
            case "mecanicos" -> {
                titulo="Ordenes por mecanico";
                sql="SELECT m.nombre AS Mecanico, o.id AS Orden, v.placa AS Placa, o.estado AS Estado " +
                    "FROM ordenes_trabajo o JOIN mecanicos m ON o.mecanico_id=m.id JOIN vehiculos v ON o.vehiculo_id=v.id " +
                    "ORDER BY m.nombre,o.estado,o.id";
            }
            case "ingresos" -> {
                titulo="Ingresos por reparaciones finalizadas";
                LocalDate inicio,fin;
                try {inicio=LocalDate.parse(filtros.getOrDefault("inicio",""));fin=LocalDate.parse(filtros.getOrDefault("fin",""));}
                catch(Exception e){throw new ApiException(400,"Selecciona fechas válidas de inicio y fin");}
                if(fin.isBefore(inicio)) throw new ApiException(400,"La fecha fin no puede ser anterior a inicio");
                sql="SELECT id AS Orden, DATE_FORMAT(fecha_finalizacion,'%d/%m/%Y') AS Fecha, total AS Ingreso_Q " +
                    "FROM ordenes_trabajo WHERE estado='FINALIZADA' AND fecha_finalizacion>=? AND fecha_finalizacion<? ORDER BY fecha_finalizacion";
                parametros.add(java.sql.Timestamp.valueOf(inicio.atStartOfDay()));
                parametros.add(java.sql.Timestamp.valueOf(fin.plusDays(1).atStartOfDay()));
                titulo+=" "+inicio+" a "+fin;
            }
            default -> throw new ApiException(404,"Reporte no encontrado");
        }
        List<String> lineas=new ArrayList<>();
        try(Connection c=ConexionBD.getConnection();PreparedStatement s=c.prepareStatement(sql)) {
            for(int i=0;i<parametros.size();i++) s.setObject(i+1,parametros.get(i));
            try(ResultSet r=s.executeQuery()) {
                ResultSetMetaData md=r.getMetaData();int columnas=md.getColumnCount();
                StringJoiner cabecera=new StringJoiner(" | ");
                for(int i=1;i<=columnas;i++)cabecera.add(md.getColumnLabel(i));
                lineas.add(cabecera.toString());lineas.add("--------------------------------------------------------------------------------");
                int registros=0;
                java.math.BigDecimal total=java.math.BigDecimal.ZERO;
                while(r.next()) {
                    registros++;
                    StringJoiner fila=new StringJoiner(" | ");
                    for(int i=1;i<=columnas;i++) fila.add(Objects.toString(r.getObject(i),""));
                    lineas.add(fila.toString());
                    if(tipo.equals("ingresos")) total=total.add(r.getBigDecimal("Ingreso_Q"));
                }
                if(registros==0)lineas.add("Sin registros para los filtros seleccionados.");
                lineas.add("");lineas.add("Registros: "+registros);
                if(tipo.equals("ingresos"))lineas.add("TOTAL INGRESOS: Q "+total);
            }
        }
        return PdfUtil.generar(titulo,lineas);
    }

    public byte[] comprobante(long id) throws Exception {
        Map<String,Object> orden = new OrdenDAO().detalle(id);
        if(!"FINALIZADA".equals(orden.get("estado"))) throw new ApiException(409,"El comprobante solo existe para una orden FINALIZADA");
        List<String> lineas=new ArrayList<>();
        lineas.add("COMPROBANTE DE REPARACION / ORDEN #"+id);
        lineas.add("Cliente: "+orden.get("cliente"));
        lineas.add("Placa: "+orden.get("placa")+"   Tipo: "+orden.get("tipo"));
        lineas.add("Mecanico: "+orden.get("mecanico"));
        lineas.add("Problema: "+orden.get("problema"));
        lineas.add("Fecha de finalizacion: "+orden.get("fecha_finalizacion"));
        lineas.add("");lineas.add("SERVICIOS:");
        for(Object obj:(List<?>)orden.get("servicios")) {
            Map<?,?> s=(Map<?,?>)obj;lineas.add(" - "+s.get("descripcion")+"   Q "+s.get("costo"));
        }
        lineas.add("");lineas.add("REPUESTOS:");
        for(Object obj:(List<?>)orden.get("repuestos")) {
            Map<?,?> r=(Map<?,?>)obj;
            lineas.add(" - "+r.get("nombre")+"  x"+r.get("cantidad")+"   Q "+r.get("precio")+" c/u");
        }
        lineas.add("");lineas.add("TOTAL: Q "+orden.get("total"));
        return PdfUtil.generar("Comprobante final",lineas);
    }
}
