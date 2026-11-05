package com.ultimoarranque.controller;

import com.sun.net.httpserver.*;
import com.ultimoarranque.report.ReportesDAO;
import com.ultimoarranque.util.*;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

public class ReportesController implements HttpHandler {
    private final ReportesDAO dao=new ReportesDAO();
    @Override public void handle(HttpExchange x) throws IOException {
        if(HttpUtil.handleOptions(x))return;
        try {
            if(!x.getRequestMethod().equals("GET"))throw new ApiException(405,"Método no permitido");
            String ruta=x.getRequestURI().getPath().substring("/api/reportes".length());
            byte[] bytes;
            if(ruta.matches("/comprobante/[0-9]+")) {
                bytes=dao.comprobante(Long.parseLong(ruta.substring("/comprobante/".length())));
            } else if(ruta.matches("/[a-z-]+")) {
                bytes=dao.reporte(ruta.substring(1),filtros(x.getRequestURI().getRawQuery()));
            } else throw new ApiException(404,"Reporte no encontrado");
            HttpUtil.aplicarCors(x);
            x.getResponseHeaders().set("Content-Type","application/pdf");
            x.getResponseHeaders().set("Content-Disposition","inline; filename=el_ultimo_arranque.pdf");
            x.sendResponseHeaders(200,bytes.length);
            try(var out=x.getResponseBody()){out.write(bytes);}
        } catch(ApiException e){HttpUtil.send(x,e.getStatus(),Map.of("message",e.getMessage()));}
        catch(SQLException e){System.err.println("Error SQL reportes: "+e.getMessage());HttpUtil.send(x,503,Map.of("message","No se pudieron leer los datos del reporte"));}
        catch(Exception e){System.err.println("Error al generar PDF: "+e.getMessage());HttpUtil.send(x,500,Map.of("message","Error al generar PDF"));}
    }
    private Map<String,String> filtros(String query){
        Map<String,String> f=new HashMap<>();
        if(query==null||query.isBlank())return f;
        for(String kv:query.split("&")) {
            String[] partes=kv.split("=",2);
            if(partes.length==2)f.put(URLDecoder.decode(partes[0],StandardCharsets.UTF_8),URLDecoder.decode(partes[1],StandardCharsets.UTF_8));
        }
        return f;
    }
}
