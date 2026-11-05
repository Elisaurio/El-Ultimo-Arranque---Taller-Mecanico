package com.ultimoarranque.model;
import java.math.BigDecimal;
public class Servicio {
    private Long id;
    private String descripcion;
    private BigDecimal costo;
    public Servicio() {}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String descripcion){this.descripcion=descripcion;}
    public BigDecimal getCosto(){return costo;}
    public void setCosto(BigDecimal costo){this.costo=costo;}
}
