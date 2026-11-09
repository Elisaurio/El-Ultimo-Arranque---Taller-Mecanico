package com.ultimoarranque.model;
import java.math.BigDecimal;

public class OrdenRepuestoDetalle {
    private Long id;
    private Long repuestoId;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitarioAplicado;
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public Long getRepuestoId() { return repuestoId; }
    public void setRepuestoId(Long repuestoId) { this.repuestoId=repuestoId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre=nombre; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad=cantidad; }
    public BigDecimal getPrecioUnitarioAplicado() { return precioUnitarioAplicado; }
    public void setPrecioUnitarioAplicado(BigDecimal precioUnitarioAplicado) { this.precioUnitarioAplicado=precioUnitarioAplicado; }
    public BigDecimal getSubtotal() { return precioUnitarioAplicado.multiply(BigDecimal.valueOf(cantidad)); }
}
