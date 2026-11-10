package com.ultimoarranque.android.api;

import com.ultimoarranque.android.model.RepuestoBajo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class ReporteApiClient {
    private static final String RUTA = "/api/reportes/inventario-bajo";

    public List<RepuestoBajo> consultarInventarioBajo(String urlBase) throws IOException {
        HttpURLConnection conexion = null;
        try {
            URL url = new URL(urlBase + RUTA);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setRequestMethod("GET");
            conexion.setRequestProperty("Accept", "application/json");
            conexion.setConnectTimeout(6000);
            conexion.setReadTimeout(8000);

            int codigoHttp = conexion.getResponseCode();
            if (codigoHttp != HttpURLConnection.HTTP_OK) {
                throw new IOException("El servidor respondió HTTP " + codigoHttp
                        + ". Verifica la ruta " + RUTA + ".");
            }

            String json;
            try (InputStream entrada = conexion.getInputStream()) {
                json = leerTexto(entrada);
            }
            return parsearRepuestos(json);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    private String leerTexto(InputStream entrada) throws IOException {
        StringBuilder contenido = new StringBuilder();
        try (BufferedReader lector = new BufferedReader(
                new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                contenido.append(linea);
            }
        }
        return contenido.toString();
    }

    private List<RepuestoBajo> parsearRepuestos(String json) throws IOException {
        try {
            JSONArray arreglo = new JSONArray(json);
            List<RepuestoBajo> resultado = new ArrayList<>();
            for (int i = 0; i < arreglo.length(); i++) {
                JSONObject objeto = arreglo.getJSONObject(i);
                String codigo = objeto.getString("codigo");
                String nombre = objeto.getString("nombre");
                int existencia = objeto.getInt("existencia");
                BigDecimal precio = new BigDecimal(objeto.get("precio").toString());
                resultado.add(new RepuestoBajo(codigo, nombre, existencia, precio));
            }
            return resultado;
        } catch (JSONException | NumberFormatException ex) {
            throw new IOException("La API no devolvió el formato JSON esperado.", ex);
        }
    }
}
