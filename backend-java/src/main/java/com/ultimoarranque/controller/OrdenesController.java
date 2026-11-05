package com.ultimoarranque.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.*;
import com.ultimoarranque.service.OrdenService;
import com.ultimoarranque.util.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class OrdenesController implements HttpHandler {
    private final OrdenService service=new OrdenService();
    @Override public void handle(HttpExchange x) throws IOException {
        if(HttpUtil.handleOptions(x)) return;
        try {
            String path=x.getRequestURI().getPath().substring("/api/ordenes".length());
            String metodo=x.getRequestMethod();
            if(path.isEmpty()||path.equals("/")) {
                if(metodo.equals("GET")) HttpUtil.send(x,200,service.listar());
                else if(metodo.equals("POST")) HttpUtil.send(x,201,service.crear(leer(x)));
                else throw new ApiException(405,"Método no permitido");
                return;
            }
            String[] segmentos=path.substring(1).split("/");
            if(segmentos.length<1 || segmentos.length>3) throw new ApiException(404,"Ruta no encontrada");
            long id=positivo(segmentos[0]);
            if(segmentos.length==1 && metodo.equals("GET")) {HttpUtil.send(x,200,service.detalle(id));return;}
            if(segmentos.length==2 && metodo.equals("POST")) {
                switch(segmentos[1]) {
                    case "iniciar" -> HttpUtil.send(x,200,service.iniciar(id));
                    case "finalizar" -> HttpUtil.send(x,200,service.finalizar(id));
                    case "cancelar" -> HttpUtil.send(x,200,service.cancelar(id));
                    case "servicios" -> HttpUtil.send(x,200,service.agregarServicio(id,leer(x)));
                    case "repuestos" -> HttpUtil.send(x,200,service.agregarRepuesto(id,leer(x)));
                    default -> throw new ApiException(404,"Ruta no encontrada");
                }
                return;
            }
            if(segmentos.length==3 && metodo.equals("DELETE")) {
                long detalleId=positivo(segmentos[2]);
                switch(segmentos[1]) {
                    case "servicios" -> HttpUtil.send(x,200,service.quitarServicio(id,detalleId));
                    case "repuestos" -> HttpUtil.send(x,200,service.quitarRepuesto(id,detalleId));
                    default -> throw new ApiException(404,"Ruta no encontrada");
                }
                return;
            }
            throw new ApiException(405,"Operación no permitida");
        } catch(ApiException e) {HttpUtil.send(x,e.getStatus(),Map.of("message",e.getMessage()));}
        catch(JsonProcessingException e){HttpUtil.send(x,400,Map.of("message","JSON inválido"));}
        catch(SQLException e){
            System.err.println("Error SQL de órdenes: "+e.getMessage());
            int code=e.getErrorCode();
            if(code==1062) HttpUtil.send(x,409,Map.of("message","Ese servicio ya está registrado en la orden"));
            else if(code==1452) HttpUtil.send(x,400,Map.of("message","Selecciona un vehículo, mecánico, repuesto o servicio existente"));
            else if(code==1451) HttpUtil.send(x,409,Map.of("message","Registro asociado a una orden existente"));
            else HttpUtil.send(x,503,Map.of("message","Error de MySQL. Revisa las tablas del script 03_modulos_taller.sql"));
        } catch(Exception e){System.err.println("Error en órdenes: "+e.getMessage());HttpUtil.send(x,500,Map.of("message","Error inesperado del servidor"));}
    }
    private long positivo(String s) {
        try {long n=Long.parseLong(s);if(n<1)throw new NumberFormatException();return n;}
        catch(NumberFormatException ex){throw new ApiException(400,"Identificador inválido");}
    }
    private Map<String,Object> leer(HttpExchange x) throws IOException {
        byte[] b=HttpUtil.leerCuerpo(x);
        if(b.length==0) throw new ApiException(400,"Faltan datos");
        return HttpUtil.JSON.readValue(b,new TypeReference<Map<String,Object>>(){});
    }
}
