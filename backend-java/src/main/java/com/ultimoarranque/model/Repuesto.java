package com.ultimoarranque.model;
import java.math.BigDecimal;
public class Repuesto {
    private Long id;
    private String codigo, nombre, descripcion;
    private BigDecimal precio;
    private int existencia;
    public Repuesto() {}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getCodigo(){return codigo;}
    public void setCodigo(String codigo){this.codigo=codigo;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public String getDescripcion(){return descripcion;}
    public void setDescripcion(String descripcion){this.descripcion=descripcion;}
    public BigDecimal getPrecio(){return precio;}
    public void setPrecio(BigDecimal precio){this.precio=precio;}
    public int getExistencia(){return existencia;}
    public void setExistencia(int existencia){this.existencia=existencia;}
}
