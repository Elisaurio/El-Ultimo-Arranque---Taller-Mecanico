package com.ultimoarranque.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public final class HttpUtil {
    public static final ObjectMapper JSON = new ObjectMapper();

    private HttpUtil() {}

    public static boolean handleOptions(HttpExchange exchange) throws IOException {
        if (!"OPTIONS".equals(exchange.getRequestMethod())) return false;
        aplicarCors(exchange);
        exchange.sendResponseHeaders(204, -1);
        exchange.close();
        return true;
    }

    public static void send(HttpExchange exchange, int status, Object response) throws IOException {
        byte[] body = JSON.writeValueAsString(response).getBytes(StandardCharsets.UTF_8);
        aplicarCors(exchange);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, body.length);
        try (var output = exchange.getResponseBody()) {
            output.write(body);
        }
    }

    public static byte[] leerCuerpo(HttpExchange exchange) throws IOException {
        byte[] body = exchange.getRequestBody().readNBytes(16385);
        if (body.length > 16384) throw new ApiException(413, "Solicitud demasiado grande");
        return body;
    }

    public static void aplicarCors(HttpExchange exchange) {
        String origin = exchange.getRequestHeaders().getFirst("Origin");
        if (origin != null && origin.matches("^https?://(localhost|127\\.0\\.0\\.1)(:[0-9]{1,5})?$")) {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", origin);
            exchange.getResponseHeaders().set("Vary", "Origin");
        }
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
    }
}
