package com.ultimoarranque.service;

import com.ultimoarranque.dto.CrearOrdenForm;
import com.ultimoarranque.model.EstadoOrden;
import com.ultimoarranque.model.OrdenRepuestoDetalle;
import com.ultimoarranque.model.OrdenServicioDetalle;
import com.ultimoarranque.model.OrdenTrabajo;
import com.ultimoarranque.model.Repuesto;
import com.ultimoarranque.model.Servicio;
import com.ultimoarranque.repository.MecanicoRepository;
import com.ultimoarranque.repository.OrdenRepository;
import com.ultimoarranque.repository.RepuestoExistenciaRepository;
import com.ultimoarranque.repository.RepuestoRepository;
import com.ultimoarranque.repository.ServicioRepository;
import com.ultimoarranque.repository.VehiculoRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrdenService {
    private final OrdenRepository ordenes;
    private final VehiculoRepository vehiculos;
    private final MecanicoRepository mecanicos;
    private final ServicioRepository servicios;
    private final RepuestoRepository repuestos;
    private final RepuestoExistenciaRepository inventario;

    public OrdenService(OrdenRepository ordenes, VehiculoRepository vehiculos,
            MecanicoRepository mecanicos, ServicioRepository servicios,
            RepuestoRepository repuestos, RepuestoExistenciaRepository inventario) {
        this.ordenes = ordenes;
        this.vehiculos = vehiculos;
        this.mecanicos = mecanicos;
        this.servicios = servicios;
        this.repuestos = repuestos;
        this.inventario = inventario;
    }

    @Transactional(readOnly=true)
    public List<OrdenTrabajo> listar() { return ordenes.listar(); }

    @Transactional(readOnly=true)
    public int contar() { return ordenes.contar(); }

    @Transactional(readOnly=true)
    public OrdenTrabajo obtener(long id) {
        return ordenes.buscar(id).orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
    }

    @Transactional(readOnly=true)
    public List<OrdenServicioDetalle> serviciosDe(long id) { return ordenes.listarServicios(id); }

    @Transactional(readOnly=true)
    public List<OrdenRepuestoDetalle> repuestosDe(long id) { return ordenes.listarRepuestos(id); }

    @Transactional(readOnly=true)
    public BigDecimal totalManoObra(long id) { return ordenes.totalServicios(id); }

    @Transactional(readOnly=true)
    public BigDecimal totalRepuestos(long id) { return ordenes.totalRepuestos(id); }

    @Transactional
    public long crear(CrearOrdenForm form) {
        if (form.getVehiculoId()==null || vehiculos.buscar(form.getVehiculoId()).isEmpty())
            throw new IllegalArgumentException("Selecciona un vehículo registrado");
        if (form.getMecanicoId()==null || mecanicos.buscar(form.getMecanicoId()).isEmpty())
            throw new IllegalArgumentException("Selecciona un mecánico registrado");
        if (form.getProblema()==null || form.getProblema().isBlank())
            throw new IllegalArgumentException("Describe el problema del vehículo");
        return ordenes.crear(form.getVehiculoId(),form.getMecanicoId(),form.getProblema().trim());
    }

    private OrdenTrabajo bloquear(long id) {
        return ordenes.bloquear(id).orElseThrow(() -> new IllegalArgumentException("Orden no encontrada"));
    }

    private void exigirEnProceso(OrdenTrabajo o) {
        if (o.getEstado()!=EstadoOrden.EN_PROCESO)
            throw new IllegalArgumentException("Solo se pueden modificar órdenes EN PROCESO");
    }

    @Transactional
    public void iniciar(long id) {
        OrdenTrabajo o = bloquear(id);
        if (o.getEstado()!=EstadoOrden.PENDIENTE)
            throw new IllegalArgumentException("Solo se puede iniciar una orden PENDIENTE");
        ordenes.iniciar(id);
    }

    @Transactional
    public void agregarServicio(long id, long servicioId) {
        OrdenTrabajo o = bloquear(id);
        exigirEnProceso(o);
        Servicio s = servicios.buscar(servicioId)
            .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        ordenes.agregarServicio(id,servicioId,s.getCostoManoObra());
    }

    @Transactional
    public void quitarServicio(long id, long detalleId) {
        exigirEnProceso(bloquear(id));
        if (ordenes.quitarServicio(id,detalleId)!=1)
            throw new IllegalArgumentException("Servicio no asignado a esta orden");
    }

    @Transactional
    public void agregarRepuesto(long id, long repuestoId, int cantidad) {
        OrdenTrabajo o = bloquear(id);
        exigirEnProceso(o);
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        Repuesto r = repuestos.buscar(repuestoId)
            .orElseThrow(() -> new IllegalArgumentException("Repuesto no encontrado"));
        if (!inventario.descontarSiDisponible(repuestoId,cantidad))
            throw new IllegalArgumentException("Inventario insuficiente para " + r.getNombre());
        ordenes.agregarRepuesto(id,repuestoId,cantidad,r.getPrecio());
    }

    @Transactional
    public void quitarRepuesto(long id, long detalleId) {
        exigirEnProceso(bloquear(id));
        OrdenRepuestoDetalle encontrado = ordenes.listarRepuestos(id).stream()
            .filter(d -> d.getId().equals(detalleId)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Repuesto no asignado a esta orden"));
        if (ordenes.quitarRepuesto(id,detalleId)!=1)
            throw new IllegalArgumentException("No se pudo quitar el repuesto");
        inventario.reintegrar(encontrado.getRepuestoId(),encontrado.getCantidad());
    }

    @Transactional
    public void finalizar(long id) {
        OrdenTrabajo o = bloquear(id);
        exigirEnProceso(o);
        if (ordenes.contarServicios(id) == 0)
            throw new IllegalArgumentException("No se puede finalizar una orden sin servicios registrados");
        ordenes.finalizar(id,ordenes.totalServicios(id),ordenes.totalRepuestos(id));
    }

    @Transactional
    public void cancelar(long id) {
        OrdenTrabajo o = bloquear(id);
        if (o.getEstado()!=EstadoOrden.PENDIENTE && o.getEstado()!=EstadoOrden.EN_PROCESO)
            throw new IllegalArgumentException("Solo pueden cancelarse órdenes pendientes o en proceso");
        for (OrdenRepuestoDetalle d : ordenes.listarRepuestos(id)) {
            inventario.reintegrar(d.getRepuestoId(),d.getCantidad());
        }
        ordenes.cancelar(id);
    }
}
