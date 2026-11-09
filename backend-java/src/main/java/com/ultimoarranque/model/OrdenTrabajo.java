package com.ultimoarranque.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdenTrabajo {
    private Long id;
    private Long vehiculoId;
    private Long mecanicoId;
    private String problema;
    private EstadoOrden estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFinalizacion;
    private BigDecimal totalManoObra;
    private BigDecimal totalRepuestos;
    private BigDecimal total;
    private String placa;
    private String clienteNombre;
    private String mecanicoNombre;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public Long getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Long mecanicoId) { this.mecanicoId = mecanicoId; }
    public String getProblema() { return problema; }
    public void setProblema(String problema) { this.problema = problema; }
    public EstadoOrden getEstado() { return estado; }
    public void setEstado(EstadoOrden estado) { this.estado = estado; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaFinalizacion() { return fechaFinalizacion; }
    public void setFechaFinalizacion(LocalDateTime fechaFinalizacion) { this.fechaFinalizacion = fechaFinalizacion; }
    public BigDecimal getTotalManoObra() { return totalManoObra; }
    public void setTotalManoObra(BigDecimal totalManoObra) { this.totalManoObra = totalManoObra; }
    public BigDecimal getTotalRepuestos() { return totalRepuestos; }
    public void setTotalRepuestos(BigDecimal totalRepuestos) { this.totalRepuestos = totalRepuestos; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public String getMecanicoNombre() { return mecanicoNombre; }
    public void setMecanicoNombre(String mecanicoNombre) { this.mecanicoNombre = mecanicoNombre; }
}
