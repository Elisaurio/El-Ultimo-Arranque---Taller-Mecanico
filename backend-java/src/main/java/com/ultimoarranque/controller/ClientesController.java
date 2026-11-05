package com.ultimoarranque.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ultimoarranque.model.Cliente;
import com.ultimoarranque.service.ClienteService;
import com.ultimoarranque.util.ApiException;
import com.ultimoarranque.util.HttpUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

public class ClientesController implements HttpHandler {
    private final ClienteService service = new ClienteService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (HttpUtil.handleOptions(exchange)) return;
        try {
            String path = exchange.getRequestURI().getPath();
            String restante = path.substring("/api/clientes".length());
            String metodo = exchange.getRequestMethod();
            if (restante.isEmpty() || "/".equals(restante)) {
                switch (metodo) {
                    case "GET" -> HttpUtil.send(exchange, 200, service.listar());
                    case "POST" -> HttpUtil.send(exchange, 201, service.crear(leerCliente(exchange)));
                    default -> throw new ApiException(405, "Método no permitido");
                }
                return;
            }
            if (!restante.matches("/[0-9]+")) throw new ApiException(404, "Ruta no encontrada");
            long id;
            try {
                id = Long.parseLong(restante.substring(1));
            } catch (NumberFormatException ex) {
                throw new ApiException(400, "Identificador de cliente inválido");
            }
            if (id <= 0) throw new ApiException(400, "Identificador de cliente inválido");
            switch (metodo) {
                case "GET" -> HttpUtil.send(exchange, 200, service.buscar(id));
                case "PUT" -> HttpUtil.send(exchange, 200, service.actualizar(id, leerCliente(exchange)));
                case "DELETE" -> {
                    service.eliminar(id);
                    HttpUtil.send(exchange, 200, Map.of("message", "Cliente eliminado correctamente"));
                }
                default -> throw new ApiException(405, "Método no permitido");
            }
        } catch (ApiException ex) {
            HttpUtil.send(exchange, ex.getStatus(), Map.of("message", ex.getMessage()));
        } catch (JsonProcessingException ex) {
            HttpUtil.send(exchange, 400, Map.of("message", "El JSON enviado no es válido"));
        } catch (SQLException ex) {
            System.err.println("Error en MySQL: " + ex.getMessage());
            if (ex.getErrorCode() == 1062) {
                HttpUtil.send(exchange, 409, Map.of("message", "Ese identificador ya está registrado"));
            } else if (ex.getErrorCode() == 1451) {
                HttpUtil.send(exchange, 409, Map.of("message", "No se puede eliminar un cliente con vehículos asociados"));
            } else {
                HttpUtil.send(exchange, 503, Map.of("message", "No se pudo consultar MySQL. Revisa la conexión y las tablas del archivo SQL."));
            }
        } catch (Exception ex) {
            System.err.println("Error del servidor: " + ex.getMessage());
            HttpUtil.send(exchange, 500, Map.of("message", "Error inesperado del servidor"));
        }
    }

    private Cliente leerCliente(HttpExchange exchange) throws IOException {
        byte[] cuerpo = HttpUtil.leerCuerpo(exchange);
        if (cuerpo.length == 0) throw new ApiException(400, "Debe enviar los datos del cliente");
        return HttpUtil.JSON.readValue(cuerpo, Cliente.class);
    }
}
