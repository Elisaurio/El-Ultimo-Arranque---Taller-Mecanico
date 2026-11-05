package com.ultimoarranque.report;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class PdfUtil {
    private static final PDType1Font TITULO = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private static final PDType1Font TEXTO = new PDType1Font(Standard14Fonts.FontName.COURIER);
    private PdfUtil() {}

    public static byte[] generar(String nombre, List<String> filas) throws IOException {
        List<String> lineas=new ArrayList<>();
        for(String fila:filas) {
            String limpia=ascii(fila);
            if(limpia.isEmpty()) {lineas.add("");continue;}
            while(limpia.length()>95) {lineas.add(limpia.substring(0,95));limpia=limpia.substring(95);}
            lineas.add(limpia);
        }
        if(lineas.isEmpty()) lineas.add("Sin resultados para los filtros seleccionados.");
        try(PDDocument doc=new PDDocument()) {
            int indice=0;int pagina=1;
            do {
                PDPage hoja=new PDPage(PDRectangle.LETTER);doc.addPage(hoja);
                try(PDPageContentStream content=new PDPageContentStream(doc,hoja)) {
                    content.beginText();content.setFont(TITULO,16);content.newLineAtOffset(40,747);
                    content.showText("EL ULTIMO ARRANQUE");content.endText();
                    content.beginText();content.setFont(TITULO,12);content.newLineAtOffset(40,721);
                    content.showText(ascii(nombre).substring(0,Math.min(70,ascii(nombre).length())));content.endText();
                    content.beginText();content.setFont(TEXTO,8);content.newLineAtOffset(40,704);
                    content.showText("Generado: "+LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))+"    Pagina "+pagina);
                    content.endText();
                    content.moveTo(40,692);content.lineTo(572,692);content.stroke();
                    int y=674;
                    for(int i=0;i<46 && indice<lineas.size();i++) {
                        content.beginText();content.setFont(TEXTO,9);content.newLineAtOffset(40,y);
                        content.showText(lineas.get(indice++));content.endText();y-=13;
                    }
                }
                pagina++;
            } while(indice<lineas.size());
            ByteArrayOutputStream salida=new ByteArrayOutputStream();doc.save(salida);return salida.toByteArray();
        }
    }
    private static String ascii(String s) {
        String v=Normalizer.normalize(s==null?"":s,Normalizer.Form.NFD).replaceAll("\\p{M}","");
        return v.replace('Ñ','N').replace('ñ','n').replaceAll("[^\\x20-\\x7E]","?");
    }
}
