package com.ultimoarranque.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import com.ultimoarranque.model.reporte.RepuestoBajo;
import com.ultimoarranque.repository.ReporteRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ReporteServiceTest {

    private final ReporteRepository repository = Mockito.mock(ReporteRepository.class);
    private final ReporteService service = new ReporteService(repository);

    @Test
    void rechazaRangoInvertido() {
        assertThrows(IllegalArgumentException.class,
                () -> service.ingresos(LocalDate.of(2026,11,8),LocalDate.of(2026,11,1)));
    }

    @Test
    void detectaExistenciaBaja() {
        when(repository.inventarioBajo()).thenReturn(List.of(
                new RepuestoBajo("REP-1","Filtro",0,new BigDecimal("45.50")),
                new RepuestoBajo("REP-2","Bujía",5,new BigDecimal("20.00"))));
        var tabla = service.inventarioBajo();
        assertEquals(2,tabla.filas().size());
        assertEquals("0",tabla.filas().get(0).get(2));
        assertEquals(true,tabla.resumen().contains("Agotados: 1"));
    }
}
