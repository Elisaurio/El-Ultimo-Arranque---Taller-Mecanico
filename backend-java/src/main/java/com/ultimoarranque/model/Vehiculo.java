package com.ultimoarranque.model;
import jakarta.validation.constraints.*;

public class Vehiculo {
    private Long id;
    @NotNull(message = "Selecciona un cliente")
    private Long clienteId;
    @NotBlank(message = "La placa es obligatoria") @Size(max = 20)
    private String placa;
    @NotBlank(message = "La marca es obligatoria") @Size(max = 70)
    private String marca;
    @NotBlank(message = "El modelo es obligatorio") @Size(max = 70)
    private String modelo;
    @Min(value=1900, message="Año no válido") @Max(value=2100, message="Año no válido")
    private Integer anio;
    @Size(max = 50)
    private String color;
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long value) { this.clienteId=value; }
    public String getPlaca() { return placa; }
    public void setPlaca(String value) { this.placa=value; }
    public String getMarca() { return marca; }
    public void setMarca(String value) { this.marca=value; }
    public String getModelo() { return modelo; }
    public void setModelo(String value) { this.modelo=value; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer value) { this.anio=value; }
    public String getColor() { return color; }
    public void setColor(String value) { this.color=value; }
}
