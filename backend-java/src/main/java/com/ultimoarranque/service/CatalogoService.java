package com.ultimoarranque.service;

import com.ultimoarranque.dao.CatalogoDAO;
import com.ultimoarranque.util.ApiException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Year;
import java.util.*;

public class CatalogoService {
    private final String modulo;
    private final CatalogoDAO dao;

    public CatalogoService(String modulo) { this.modulo = modulo; dao = new CatalogoDAO(modulo); }

    public List<Map<String,Object>> listar(Long clienteId) throws SQLException { return dao.listar(clienteId); }
    public Map<String,Object> buscar(long id) throws SQLException { return dao.buscar(id).orElseThrow(() -> new ApiException(404,"Registro no encontrado")); }
    public Map<String,Object> crear(Map<String,Object> json) throws SQLException { return dao.crear(validar(json)); }
    public Map<String,Object> actualizar(long id, Map<String,Object> json) throws SQLException {
        buscar(id);
        Map<String,Object> datos = validar(json);
        if (!dao.actualizar(id, datos)) throw new ApiException(404,"Registro no encontrado");
        return buscar(id);
    }
    public Map<String,Object> abastecer(long id,Map<String,Object> json) throws SQLException {
        if(!modulo.equals("repuestos")) throw new ApiException(404,"Ruta no encontrada");
        buscar(id);
        int cantidad=enteroPositivo(json,"cantidad");
        if(!dao.abastecer(id,cantidad)) throw new ApiException(404,"Repuesto no encontrado");
        return buscar(id);
    }
    public void eliminar(long id) throws SQLException {
        buscar(id);
        if (!dao.eliminar(id)) throw new ApiException(404,"Registro no encontrado");
    }

    private Map<String,Object> validar(Map<String,Object> json) {
        if (json == null) throw new ApiException(400,"Datos requeridos");
        Map<String,Object> datos = new LinkedHashMap<>();
        switch (modulo) {
            case "vehiculos" -> {
                datos.put("cliente_id", enteroPositivo(json,"cliente_id"));
                datos.put("tipo", texto(json,"tipo",40,true));
                datos.put("placa", texto(json,"placa",20,true).toUpperCase(Locale.ROOT));
                datos.put("marca", texto(json,"marca",80,false));
                datos.put("modelo", texto(json,"modelo",80,false));
                Object anio = json.get("anio");
                if (anio == null || anio.toString().isBlank()) datos.put("anio",null);
                else {
                    int n = enteroPositivo(json,"anio");
                    if (n < 1900 || n > Year.now().getValue()+1) throw new ApiException(400,"Año del vehículo inválido");
                    datos.put("anio",n);
                }
            }
            case "mecanicos" -> {
                datos.put("nombre", texto(json,"nombre",120,true));
                datos.put("especialidad", texto(json,"especialidad",100,true));
                datos.put("telefono", texto(json,"telefono",25,false));
            }
            case "servicios" -> {
                datos.put("descripcion", texto(json,"descripcion",150,true));
                datos.put("costo", dinero(json,"costo"));
            }
            case "repuestos" -> {
                datos.put("codigo", texto(json,"codigo",35,true));
                datos.put("nombre", texto(json,"nombre",120,true));
                datos.put("descripcion", texto(json,"descripcion",255,false));
                datos.put("precio", dinero(json,"precio"));
                datos.put("existencia", enteroNoNegativo(json,"existencia"));
            }
            default -> throw new ApiException(404,"Módulo no encontrado");
        }
        return datos;
    }
    private String texto(Map<String,Object> json, String campo,int max,boolean obligatorio) {
        String v = Objects.toString(json.get(campo),"").trim();
        if (obligatorio && v.isEmpty()) throw new ApiException(400,"El campo "+campo+" es obligatorio");
        if (v.length()>max) throw new ApiException(400,"El campo "+campo+" excede "+max+" caracteres");
        return v;
    }
    private int enteroPositivo(Map<String,Object> json,String campo) {
        int n=enteroNoNegativo(json,campo);
        if(n==0) throw new ApiException(400,"El campo "+campo+" debe ser mayor que cero");
        return n;
    }
    private int enteroNoNegativo(Map<String,Object> json,String campo) {
        try {
            String v=Objects.toString(json.get(campo),"");
            int n=Integer.parseInt(v);
            if (n<0) throw new NumberFormatException();
            return n;
        } catch(Exception e){ throw new ApiException(400,"El campo "+campo+" debe ser un entero no negativo"); }
    }
    private BigDecimal dinero(Map<String,Object> json,String campo) {
        try {
            BigDecimal v = new BigDecimal(Objects.toString(json.get(campo), ""));
            if(v.signum()<0 || v.scale()>2 || v.precision()>12) throw new NumberFormatException();
            return v;
        } catch(Exception e) { throw new ApiException(400,"El campo "+campo+" debe ser un monto válido (dos decimales)"); }
    }
}
