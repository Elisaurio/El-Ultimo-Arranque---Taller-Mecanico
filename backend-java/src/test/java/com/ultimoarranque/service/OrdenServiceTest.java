package com.ultimoarranque.service;

import com.ultimoarranque.model.EstadoOrden;
import com.ultimoarranque.model.OrdenRepuestoDetalle;
import com.ultimoarranque.model.OrdenTrabajo;
import com.ultimoarranque.model.Repuesto;
import com.ultimoarranque.repository.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrdenServiceTest {
    private final OrdenRepository ordenes = mock(OrdenRepository.class);
    private final VehiculoRepository vehiculos = mock(VehiculoRepository.class);
    private final MecanicoRepository mecanicos = mock(MecanicoRepository.class);
    private final ServicioRepository servicios = mock(ServicioRepository.class);
    private final RepuestoRepository repuestos = mock(RepuestoRepository.class);
    private final RepuestoExistenciaRepository inventario = mock(RepuestoExistenciaRepository.class);
    private final OrdenService service = new OrdenService(ordenes,vehiculos,mecanicos,servicios,repuestos,inventario);

    private OrdenTrabajo orden(long id, EstadoOrden estado) {
        OrdenTrabajo o=new OrdenTrabajo();o.setId(id);o.setEstado(estado);return o;
    }

    @Test
    void rechazaInventarioInsuficiente() {
        when(ordenes.bloquear(7)).thenReturn(Optional.of(orden(7,EstadoOrden.EN_PROCESO)));
        Repuesto r=new Repuesto();r.setNombre("Filtro");r.setPrecio(BigDecimal.TEN);
        when(repuestos.buscar(5)).thenReturn(Optional.of(r));
        when(inventario.descontarSiDisponible(5,10)).thenReturn(false);
        assertThrows(IllegalArgumentException.class,()->service.agregarRepuesto(7,5,10));
        verify(ordenes,never()).agregarRepuesto(anyLong(),anyLong(),anyInt(),any());
    }

    @Test
    void cancelarReintegraInventario() {
        when(ordenes.bloquear(7)).thenReturn(Optional.of(orden(7,EstadoOrden.EN_PROCESO)));
        OrdenRepuestoDetalle d=new OrdenRepuestoDetalle();d.setRepuestoId(5L);d.setCantidad(3);
        when(ordenes.listarRepuestos(7)).thenReturn(List.of(d));
        service.cancelar(7);
        verify(inventario).reintegrar(5,3);
        verify(ordenes).cancelar(7);
    }

    @Test
    void noFinalizaSinServicios() {
        when(ordenes.bloquear(9)).thenReturn(Optional.of(orden(9,EstadoOrden.EN_PROCESO)));
        when(ordenes.contarServicios(9)).thenReturn(0);
        assertThrows(IllegalArgumentException.class,()->service.finalizar(9));
        verify(ordenes,never()).finalizar(anyLong(),any(),any());
    }

    @Test
    void noEditaOrdenFinalizada() {
        when(ordenes.bloquear(9)).thenReturn(Optional.of(orden(9,EstadoOrden.FINALIZADA)));
        assertThrows(IllegalArgumentException.class,()->service.agregarRepuesto(9,5,1));
        verifyNoInteractions(inventario);
    }
}
