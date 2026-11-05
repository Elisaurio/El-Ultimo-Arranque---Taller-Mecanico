package com.ultimoarranque.service;

import com.ultimoarranque.dao.OrdenDAO;
import com.ultimoarranque.util.ApiException;
import java.sql.SQLException;
import java.util.*;

public class OrdenService {
    private final OrdenDAO dao=new OrdenDAO();
    public List<Map<String,Object>> listar() throws SQLException {return dao.listar();}
    public Map<String,Object> detalle(long id) throws SQLException {return dao.detalle(id);}
    public Map<String,Object> crear(Map<String,Object> m) throws SQLException {
        long vehiculo=id(m,"vehiculoId"); long mecanico=id(m,"mecanicoId");
        String problema=Objects.toString(m.get("problema"),"").trim();
        if(problema.isEmpty() || problema.length()>1000) throw new ApiException(400,"Describe el problema (máximo 1000 caracteres)");
        return dao.crear(vehiculo,mecanico,problema);
    }
    public Map<String,Object> iniciar(long id) throws SQLException {dao.iniciar(id);return dao.detalle(id);}
    public Map<String,Object> cancelar(long id) throws SQLException {dao.cancelar(id);return dao.detalle(id);}
    public Map<String,Object> finalizar(long id) throws SQLException {dao.finalizar(id);return dao.detalle(id);}
    public Map<String,Object> agregarServicio(long id,Map<String,Object> m) throws SQLException {
        dao.agregarServicio(id,id(m,"servicioId"));return dao.detalle(id);
    }
    public Map<String,Object> quitarServicio(long id,long servicioId) throws SQLException {dao.quitarServicio(id,servicioId);return dao.detalle(id);}
    public Map<String,Object> agregarRepuesto(long id,Map<String,Object> m) throws SQLException {
        long repuesto=id(m,"repuestoId"); long cantidad=id(m,"cantidad");
        if(cantidad>Integer.MAX_VALUE) throw new ApiException(400,"Cantidad demasiado grande");
        dao.agregarRepuesto(id,repuesto,(int)cantidad);return dao.detalle(id);
    }
    public Map<String,Object> quitarRepuesto(long id,long repuestoId) throws SQLException {dao.quitarRepuesto(id,repuestoId);return dao.detalle(id);}
    private long id(Map<String,Object> m,String clave) {
        try {
            if(m == null || m.get(clave) == null) throw new NumberFormatException();
            long n=Long.parseLong(m.get(clave).toString());if(n<=0) throw new NumberFormatException();return n;
        } catch(NumberFormatException e) {throw new ApiException(400,"El campo "+clave+" debe ser un entero positivo");}
    }
}
