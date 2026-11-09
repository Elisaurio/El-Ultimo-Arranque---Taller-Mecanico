package com.ultimoarranque.model;
import java.math.BigDecimal;

public class OrdenServicioDetalle {
    private Long id;
    private Long servicioId;
    private String descripcion;
    private BigDecimal costoAplicado;
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public Long getServicioId() { return servicioId; }
    public void setServicioId(Long servicioId) { this.servicioId=servicioId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion=descripcion; }
    public BigDecimal getCostoAplicado() { return costoAplicado; }
    public void setCostoAplicado(BigDecimal costoAplicado) { this.costoAplicado=costoAplicado; }
}
