package com.ultimoarranque.model.reporte;

import java.util.List;

public record ReporteTabla(
        String titulo,
        String subtitulo,
        String[] columnas,
        float[] proporciones,
        List<List<String>> filas,
        String resumen) {

    public ReporteTabla {
        if (columnas.length == 0 || columnas.length != proporciones.length) {
            throw new IllegalArgumentException("Las columnas y sus anchos no coinciden");
        }
        for (List<String> fila : filas) {
            if (fila.size() != columnas.length) {
                throw new IllegalArgumentException("Una fila no coincide con las columnas");
            }
        }
    }
}
