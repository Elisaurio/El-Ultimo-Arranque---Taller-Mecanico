package com.ultimoarranque.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdenTrabajo {
    public enum Estado { PENDIENTE, EN_PROCESO, FINALIZADA, CANCELADA }
    private Long id, vehiculoId, mecanicoId;
    private String problema;
    private Estado estado = Estado.PENDIENTE;
    private LocalDateTime fechaCreacion, fechaFinalizacion;
    private BigDecimal total = BigDecimal.ZERO;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Long getVehiculoId(){return vehiculoId;}
    public void setVehiculoId(Long id){vehiculoId=id;}
    public Long getMecanicoId(){return mecanicoId;}
    public void setMecanicoId(Long id){mecanicoId=id;}
    public String getProblema(){return problema;}
    public void setProblema(String problema){this.problema=problema;}
    public Estado getEstado(){return estado;}
    public void setEstado(Estado estado){this.estado=estado;}
    public LocalDateTime getFechaCreacion(){return fechaCreacion;}
    public void setFechaCreacion(LocalDateTime fecha){fechaCreacion=fecha;}
    public LocalDateTime getFechaFinalizacion(){return fechaFinalizacion;}
    public void setFechaFinalizacion(LocalDateTime fecha){fechaFinalizacion=fecha;}
    public BigDecimal getTotal(){return total;}
    public void setTotal(BigDecimal total){this.total=total;}
}
