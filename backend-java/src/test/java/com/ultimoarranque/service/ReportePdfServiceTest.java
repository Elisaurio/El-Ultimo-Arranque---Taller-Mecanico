package com.ultimoarranque.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import com.ultimoarranque.model.reporte.ReporteTabla;
import java.util.List;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

class ReportePdfServiceTest {

    @Test
    void generaDocumentoPdfConEncabezado() throws Exception {
        ReporteTabla tabla = new ReporteTabla(
                "Inventario bajo","Existencia menor o igual a 5",
                new String[]{"Código","Repuesto","Existencia"},
                new float[]{.2f,.6f,.2f},
                List.of(List.of("F-01","Filtro de aire","3")),
                "Total de repuestos: 1");
        byte[] bytes = new ReportePdfService().generar(tabla);
        assertTrue(bytes.length > 500);
        try (PDDocument pdf = Loader.loadPDF(bytes)) {
            assertTrue(pdf.getNumberOfPages() >= 1);
            assertTrue(new PDFTextStripper().getText(pdf).contains("Inventario bajo"));
        }
    }
}
