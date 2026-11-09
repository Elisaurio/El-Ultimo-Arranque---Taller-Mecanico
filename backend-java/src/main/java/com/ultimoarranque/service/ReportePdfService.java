package com.ultimoarranque.service;

import com.ultimoarranque.model.reporte.ReporteTabla;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.springframework.stereotype.Service;

@Service
public class ReportePdfService {

    private static final float ANCHO = PDRectangle.A4.getHeight();
    private static final float ALTO = PDRectangle.A4.getWidth();
    private static final float MARGEN = 42f;
    private static final float ANCHO_TABLA = ANCHO - 2 * MARGEN;
    private static final float ALTO_LINEA = 11.5f;
    private static final PDFont NORMAL = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private static final PDFont NEGRITA = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final Color AZUL = new Color(30, 56, 120);
    private static final Color MORADO = new Color(89, 55, 168);
    private static final Color GRIS = new Color(75, 88, 111);
    private static final Color BORDE = new Color(225, 232, 243);
    private static final Color FONDO_ALTERNO = new Color(247, 249, 253);

    public byte[] generar(ReporteTabla reporte) throws IOException {
        try (PDDocument documento = new PDDocument();
                ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Pagina actual = nuevaPagina(documento, reporte);
            int indice = 0;
            try {
                if (reporte.filas().isEmpty()) {
                    texto(actual.contenido, "Sin registros para los filtros seleccionados", MARGEN + 12,
                            actual.y - 29, NORMAL, 11, GRIS);
                    actual.y -= 57;
                } else {
                    for (List<String> fila : reporte.filas()) {
                        List<List<String>> celdas = prepararFila(reporte, fila);
                        int max = celdas.stream().mapToInt(List::size).max().orElse(1);
                        float altoFila = Math.max(30, 12 + max * ALTO_LINEA);
                        if (actual.y - altoFila < 100) {
                            actual.contenido.close();
                            actual = nuevaPagina(documento, reporte);
                        }
                        dibujarFila(actual, reporte, celdas, altoFila, indice);
                        indice++;
                    }
                }
                if (actual.y - 46 < 77) {
                    actual.contenido.close();
                    actual = nuevaPagina(documento, reporte);
                }
                resumen(actual, reporte.resumen());
            } finally {
                actual.contenido.close();
            }
            for (int i = 0; i < documento.getNumberOfPages(); i++) {
                PDPage pagina = documento.getPage(i);
                try (PDPageContentStream contenido = new PDPageContentStream(documento, pagina,
                        PDPageContentStream.AppendMode.APPEND, true, true)) {
                    linea(contenido, MARGEN, 53, ANCHO - MARGEN, 53, BORDE);
                    texto(contenido, "El Último Arranque | Reporte del taller", MARGEN, 34,
                            NORMAL, 8, GRIS);
                    String contador = "Página " + (i + 1) + " de " + documento.getNumberOfPages();
                    texto(contenido, contador, ANCHO - MARGEN - ancho(contador, NORMAL, 8),
                            34, NORMAL, 8, GRIS);
                }
            }
            documento.save(salida);
            return salida.toByteArray();
        }
    }

    private Pagina nuevaPagina(PDDocument documento, ReporteTabla reporte) throws IOException {
        PDPage pagina = new PDPage(new PDRectangle(ANCHO, ALTO));
        documento.addPage(pagina);
        PDPageContentStream contenido = new PDPageContentStream(documento, pagina);
        rectangulo(contenido, 0, ALTO - 116, ANCHO, 116, AZUL);
        rectangulo(contenido, ANCHO - 17, ALTO - 116, 17, 116, MORADO);
        texto(contenido, "EL ÚLTIMO ARRANQUE  /  REPORTES", MARGEN, ALTO - 34,
                NEGRITA, 9, Color.WHITE);
        texto(contenido, reporte.titulo(), MARGEN, ALTO - 65,
                NEGRITA, 20, Color.WHITE);
        texto(contenido, reporte.subtitulo(), MARGEN, ALTO - 85,
                NORMAL, 10, Color.WHITE);
        String fecha = "Emitido: " + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        texto(contenido, fecha, MARGEN, ALTO - 101, NORMAL, 8, Color.WHITE);
        Pagina estado = new Pagina(contenido, ALTO - 145);
        cabeceraTabla(estado, reporte);
        return estado;
    }

    private void cabeceraTabla(Pagina pagina, ReporteTabla reporte) throws IOException {
        float h = 31f;
        rectangulo(pagina.contenido, MARGEN, pagina.y - h, ANCHO_TABLA, h,
                new Color(230, 236, 249));
        float x = MARGEN;
        for (int i = 0; i < reporte.columnas().length; i++) {
            float col = ANCHO_TABLA * reporte.proporciones()[i];
            String titulo = recortar(reporte.columnas()[i], NEGRITA, 8, col - 12);
            texto(pagina.contenido, titulo, x + 6, pagina.y - 19, NEGRITA, 8, AZUL);
            x += col;
        }
        pagina.y -= h;
    }

    private List<List<String>> prepararFila(ReporteTabla reporte, List<String> fila) throws IOException {
        List<List<String>> resultado = new ArrayList<>();
        for (int i = 0; i < fila.size(); i++) {
            float ancho = ANCHO_TABLA * reporte.proporciones()[i] - 13;
            List<String> lineas = ajustar(fila.get(i), NORMAL, 8.2f, ancho);
            if (lineas.size() > 6) {
                lineas = new ArrayList<>(lineas.subList(0,6));
                lineas.set(5, recortar(lineas.get(5) + "...", NORMAL, 8.2f, ancho));
            }
            resultado.add(lineas);
        }
        return resultado;
    }

    private void dibujarFila(Pagina pagina, ReporteTabla reporte,
            List<List<String>> celdas, float alto, int numero) throws IOException {
        if (numero % 2 == 1) {
            rectangulo(pagina.contenido, MARGEN, pagina.y - alto, ANCHO_TABLA, alto, FONDO_ALTERNO);
        }
        float x = MARGEN;
        for (int i = 0; i < celdas.size(); i++) {
            float w = ANCHO_TABLA * reporte.proporciones()[i];
            int indice = 0;
            for (String valor : celdas.get(i)) {
                texto(pagina.contenido, valor, x + 6, pagina.y - 15 - indice * ALTO_LINEA,
                        NORMAL, 8.2f, GRIS);
                indice++;
            }
            x += w;
        }
        linea(pagina.contenido, MARGEN, pagina.y - alto, ANCHO - MARGEN,
                pagina.y - alto, BORDE);
        pagina.y -= alto;
    }

    private void resumen(Pagina pagina, String resumen) throws IOException {
        float y = pagina.y - 38;
        rectangulo(pagina.contenido, MARGEN, y, ANCHO_TABLA, 29,
                new Color(237, 233, 252));
        texto(pagina.contenido, recortar(resumen, NEGRITA, 9.2f, ANCHO_TABLA - 20),
                MARGEN + 10, y + 10, NEGRITA, 9.2f, MORADO);
    }

    private static List<String> ajustar(String entrada, PDFont font,
            float tamano, float anchoMaximo) throws IOException {
        String limpio = limpiar(entrada);
        if (limpio.isBlank()) return List.of("-");
        List<String> lineas = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        for (char caracter : limpio.toCharArray()) {
            String prueba = actual.toString() + caracter;
            if (ancho(prueba, font, tamano) > anchoMaximo && !actual.isEmpty()) {
                lineas.add(actual.toString().trim());
                actual.setLength(0);
                if (caracter == ' ') continue;
            }
            actual.append(caracter);
        }
        if (!actual.isEmpty()) lineas.add(actual.toString().trim());
        return lineas;
    }

    private static String recortar(String valor, PDFont font,
            float tamano, float anchoMaximo) throws IOException {
        String limpio = limpiar(valor);
        if (ancho(limpio, font, tamano) <= anchoMaximo) return limpio;
        while (!limpio.isEmpty() && ancho(limpio + "...", font, tamano) > anchoMaximo) {
            limpio = limpio.substring(0, limpio.length() - 1);
        }
        return limpio + "...";
    }

    private static String limpiar(String valor) {
        if (valor == null) return "-";
        String original = valor.replace('\n',' ').replace('\r',' ')
                .replace('–','-').replace('—','-').replace('’','\'')
                .replace('“','"').replace('”','"');
        StringBuilder seguro = new StringBuilder();
        for (char c : original.toCharArray()) {
            if ((c >= 32 && c <= 126) || (c >= 160 && c <= 255)) seguro.append(c);
            else seguro.append('?');
        }
        return seguro.toString();
    }

    private static float ancho(String valor, PDFont font, float tamano) throws IOException {
        return font.getStringWidth(valor) * tamano / 1000f;
    }

    private static void texto(PDPageContentStream flujo, String texto,
            float x, float y, PDFont font, float tamano, Color color) throws IOException {
        flujo.beginText();
        flujo.setFont(font,tamano);
        flujo.setNonStrokingColor(color);
        flujo.newLineAtOffset(x,y);
        flujo.showText(limpiar(texto));
        flujo.endText();
    }

    private static void rectangulo(PDPageContentStream flujo, float x,
            float y, float ancho, float alto, Color color) throws IOException {
        flujo.setNonStrokingColor(color);
        flujo.addRect(x,y,ancho,alto);
        flujo.fill();
    }

    private static void linea(PDPageContentStream flujo, float x1,
            float y1, float x2, float y2, Color color) throws IOException {
        flujo.setStrokingColor(color);
        flujo.setLineWidth(.6f);
        flujo.moveTo(x1,y1);
        flujo.lineTo(x2,y2);
        flujo.stroke();
    }

    private static class Pagina {
        private final PDPageContentStream contenido;
        private float y;
        private Pagina(PDPageContentStream contenido, float y) {
            this.contenido = contenido;
            this.y = y;
        }
    }
}
