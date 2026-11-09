package com.ultimoarranque.model;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class Repuesto {
    private Long id;
    @NotBlank(message = "El código es obligatorio") @Size(max = 35)
    private String codigo;
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 120)
    private String nombre;
    @Size(max = 255)
    private String descripcion;
    @NotNull @DecimalMin(value="0.00", message="El precio no puede ser negativo")
    private BigDecimal precio;
    @NotNull @Min(value=0, message="La existencia no puede ser negativa")
    private Integer existencia;
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String value) { this.codigo=value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre=value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion=value; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal value) { this.precio=value; }
    public Integer getExistencia() { return existencia; }
    public void setExistencia(Integer value) { this.existencia=value; }
}
