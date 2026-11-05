package com.ultimoarranque;

import com.sun.net.httpserver.HttpServer;
import com.ultimoarranque.controller.ClientesController;
import com.ultimoarranque.controller.CatalogoController;
import com.ultimoarranque.controller.OrdenesController;
import com.ultimoarranque.controller.ReportesController;
import com.ultimoarranque.util.HttpUtil;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", 8080), 0);
        server.createContext("/api/health", exchange -> {
            if (HttpUtil.handleOptions(exchange)) return;
            if (!"GET".equals(exchange.getRequestMethod())) {
                HttpUtil.send(exchange, 405, Map.of("message", "Método no permitido"));
                return;
            }
            HttpUtil.send(exchange, 200, Map.of("message", "Conexión exitosa con el backend Java"));
        });
        server.createContext("/api/clientes", new ClientesController());
        for (String modulo : new String[] {"vehiculos", "mecanicos", "servicios", "repuestos"}) {
            server.createContext("/api/" + modulo, new CatalogoController(modulo));
        }
        server.createContext("/api/ordenes", new OrdenesController());
        server.createContext("/api/reportes", new ReportesController());
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
        System.out.println("Backend Java iniciado en http://localhost:8080");
        System.out.println("Clientes: http://localhost:8080/api/clientes");
    }
}
