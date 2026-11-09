package com.ultimoarranque.model;
import jakarta.validation.constraints.*;

public class Mecanico {
    private Long id;
    @NotBlank(message = "El identificador es obligatorio") @Size(max = 30)
    private String identificador;
    @NotBlank(message = "El nombre es obligatorio") @Size(max = 120)
    private String nombre;
    @Size(max = 25)
    private String telefono;
    @Size(max = 120)
    private String especialidad;
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public String getIdentificador() { return identificador; }
    public void setIdentificador(String value) { this.identificador=value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre=value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { this.telefono=value; }
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String value) { this.especialidad=value; }
}
