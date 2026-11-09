package com.ultimoarranque.service;

import com.ultimoarranque.model.EstadoOrden;
import com.ultimoarranque.model.reporte.RepuestoBajo;
import com.ultimoarranque.model.reporte.ReporteTabla;
import com.ultimoarranque.repository.ReporteRepository;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReporteService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final ReporteRepository repository;

    public ReporteService(ReporteRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public ReporteTabla ordenesEstado(EstadoOrden estado) {
        var datos = repository.ordenesPorEstado(estado);
        var filas = datos.stream().map(o -> List.of(
                "#" + o.id(), o.estado(), fecha(o.fecha()), o.placa(),
                o.cliente(), o.mecanico(), moneda(o.total()))).toList();
        String filtro = estado == null ? "Todos los estados" : "Estado: " + estado.name();
        return new ReporteTabla("Órdenes por estado", filtro,
                new String[]{"Orden", "Estado", "Fecha", "Placa", "Cliente", "Mecánico", "Total"},
                new float[]{.08f,.13f,.14f,.11f,.20f,.20f,.14f},
                filas, "Total de órdenes: " + datos.size());
    }

    @Transactional(readOnly = true)
    public ReporteTabla historialVehiculo(String placa) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("Ingresa la placa para consultar el historial");
        }
        String placaNormalizada = placa.trim().toUpperCase(Locale.ROOT);
        if (placaNormalizada.length() > 20) {
            throw new IllegalArgumentException("La placa no debe superar 20 caracteres");
        }
        var datos = repository.historialPorPlaca(placaNormalizada);
        var filas = datos.stream().map(o -> List.of(
                "#" + o.id(), fecha(o.fecha()), o.estado(),
                o.mecanico(), o.problema(), moneda(o.total()))).toList();
        return new ReporteTabla("Historial de reparaciones", "Placa: " + placaNormalizada,
                new String[]{"Orden", "Fecha", "Estado", "Mecánico", "Problema reportado", "Total"},
                new float[]{.08f,.15f,.14f,.17f,.32f,.14f},
                filas, "Reparaciones encontradas: " + datos.size());
    }

    @Transactional(readOnly = true)
    public List<RepuestoBajo> inventarioBajoDatos() {
        return repository.inventarioBajo();
    }

    @Transactional(readOnly = true)
    public ReporteTabla inventarioBajo() {
        var datos = inventarioBajoDatos();
        var filas = datos.stream().map(r -> List.of(
                r.codigo(), r.nombre(), Integer.toString(r.existencia()),
                moneda(r.precio()))).toList();
        long agotados = datos.stream().filter(r -> r.existencia() == 0).count();
        return new ReporteTabla("Inventario bajo", "Repuestos con 5 unidades o menos",
                new String[]{"Código", "Repuesto", "Existencia", "Precio unitario"},
                new float[]{.19f,.44f,.16f,.21f},
                filas, "Repuestos con inventario bajo: " + datos.size() + " | Agotados: " + agotados);
    }

    @Transactional(readOnly = true)
    public ReporteTabla ordenesMecanico(Long mecanicoId) {
        if (mecanicoId != null && mecanicoId <= 0) {
            throw new IllegalArgumentException("El mecánico seleccionado no es válido");
        }
        var datos = repository.ordenesPorMecanico(mecanicoId);
        var filas = datos.stream().map(o -> List.of(
                o.identificador(), o.mecanico(), "#" + o.ordenId(),
                o.estado(), o.placa(), fecha(o.fecha()))).toList();
        String filtro = mecanicoId == null ? "Todos los mecánicos" : "Mecánico ID: " + mecanicoId;
        return new ReporteTabla("Órdenes por mecánico", filtro,
                new String[]{"Identificador", "Mecánico", "Orden", "Estado", "Placa", "Fecha"},
                new float[]{.14f,.25f,.09f,.18f,.12f,.22f},
                filas, "Órdenes encontradas: " + datos.size());
    }

    @Transactional(readOnly = true)
    public ReporteTabla ingresos(LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null) {
            throw new IllegalArgumentException("Selecciona ambas fechas");
        }
        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la final");
        }
        var datos = repository.ingresos(desde, hasta);
        var filas = datos.stream().map(o -> List.of(
                "#" + o.id(), fecha(o.fecha()), o.placa(), o.mecanico(),
                moneda(o.manoObra()), moneda(o.repuestos()), moneda(o.total()))).toList();
        BigDecimal manoObra = datos.stream().map(ReporteRepository.Ingreso::manoObra)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal repuestos = datos.stream().map(ReporteRepository.Ingreso::repuestos)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        BigDecimal total = manoObra.add(repuestos);
        return new ReporteTabla("Ingresos por reparaciones",
                "Del " + desde + " al " + hasta + " | Solo órdenes FINALIZADAS",
                new String[]{"Orden", "Finalizada", "Placa", "Mecánico", "Mano de obra", "Repuestos", "Total"},
                new float[]{.08f,.17f,.12f,.20f,.14f,.14f,.15f},
                filas, "Órdenes: " + datos.size() + " | Mano de obra: " + moneda(manoObra)
                    + " | Repuestos: " + moneda(repuestos) + " | Ingresos: " + moneda(total));
    }

    private static String fecha(LocalDateTime value) {
        return value == null ? "-" : value.format(FECHA);
    }

    private static String moneda(BigDecimal value) {
        DecimalFormat formato = new DecimalFormat("#,##0.00",DecimalFormatSymbols.getInstance(Locale.US));
        return "Q " + formato.format(value == null ? BigDecimal.ZERO : value);
    }
}
