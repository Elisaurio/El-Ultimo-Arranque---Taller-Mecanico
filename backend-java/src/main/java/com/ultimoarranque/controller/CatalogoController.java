package com.ultimoarranque.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.*;
import com.ultimoarranque.service.CatalogoService;
import com.ultimoarranque.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class CatalogoController implements HttpHandler {
    private final String modulo;
    private final CatalogoService service;
    public CatalogoController(String modulo) { this.modulo=modulo; service=new CatalogoService(modulo); }

    @Override public void handle(HttpExchange x) throws IOException {
        if(HttpUtil.handleOptions(x)) return;
        try {
            String path=x.getRequestURI().getPath();
            String resto=path.substring(("/api/"+modulo).length());
            String metodo=x.getRequestMethod();
            if(resto.isEmpty() || resto.equals("/")) {
                switch(metodo) {
                    case "GET" -> HttpUtil.send(x,200,service.listar(filtroCliente(x)));
                    case "POST" -> HttpUtil.send(x,201,service.crear(leer(x)));
                    default -> throw new ApiException(405,"Método no permitido");
                }
                return;
            }
            if(modulo.equals("repuestos") && resto.matches("/[0-9]+/abastecer") && metodo.equals("POST")) {
                long id=Long.parseLong(resto.split("/")[1]);
                HttpUtil.send(x,200,service.abastecer(id,leer(x)));return;
            }
            if(!resto.matches("/[0-9]+")) throw new ApiException(404,"Ruta no encontrada");
            long id=Long.parseLong(resto.substring(1));
            if(id<1) throw new ApiException(400,"Identificador inválido");
            switch(metodo) {
                case "GET" -> HttpUtil.send(x,200,service.buscar(id));
                case "PUT" -> HttpUtil.send(x,200,service.actualizar(id,leer(x)));
                case "DELETE" -> { service.eliminar(id); HttpUtil.send(x,200,Map.of("message","Eliminado correctamente")); }
                default -> throw new ApiException(405,"Método no permitido");
            }
        } catch(ApiException e) { HttpUtil.send(x,e.getStatus(),Map.of("message",e.getMessage())); }
        catch(JsonProcessingException e) { HttpUtil.send(x,400,Map.of("message","Datos JSON inválidos")); }
        catch(SQLException e) {
            System.err.println("Error JDBC " + modulo + ": " + e.getMessage());
            int codigo=e.getErrorCode();
            if(codigo==1062) HttpUtil.send(x,409,Map.of("message","Identificador, código o placa duplicado"));
            else if(codigo==1451) HttpUtil.send(x,409,Map.of("message","No se puede eliminar: el registro está asociado a otros datos"));
            else if(codigo==1452) HttpUtil.send(x,400,Map.of("message","El cliente u otro registro asociado no existe"));
            else HttpUtil.send(x,503,Map.of("message","No se pudo consultar MySQL. Revisa el script 03_modulos_taller.sql"));
        } catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
            HttpUtil.send(x,500,Map.of("message","Error inesperado del servidor"));
        }
    }

    private Map<String,Object> leer(HttpExchange x) throws IOException {
        byte[] datos=HttpUtil.leerCuerpo(x);
        if(datos.length==0) throw new ApiException(400,"Debes enviar datos");
        return HttpUtil.JSON.readValue(datos,new TypeReference<Map<String,Object>>() {});
    }
    private Long filtroCliente(HttpExchange x) {
        if(!modulo.equals("vehiculos")) return null;
        String q=x.getRequestURI().getRawQuery();
        if(q==null || !q.matches("clienteId=[0-9]+")) return null;
        long id=Long.parseLong(q.substring("clienteId=".length()));
        if(id<1) throw new ApiException(400,"Cliente inválido");
        return id;
    }
}
