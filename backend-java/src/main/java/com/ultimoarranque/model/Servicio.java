package com.ultimoarranque.model;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class Servicio {
    private Long id;
    @NotBlank(message = "El código es obligatorio") @Size(max = 35)
    private String codigo;
    @NotBlank(message = "La descripción es obligatoria") @Size(max = 200)
    private String descripcion;
    @NotNull @DecimalMin(value="0.00", message="El costo no puede ser negativo")
    private BigDecimal costoManoObra;
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String value) { this.codigo=value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion=value; }
    public BigDecimal getCostoManoObra() { return costoManoObra; }
    public void setCostoManoObra(BigDecimal value) { this.costoManoObra=value; }
}
