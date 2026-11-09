package com.ultimoarranque.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Cliente {
    private Long id;
    @NotBlank(message = "El identificador es obligatorio")
    @Size(max = 30, message = "Máximo 30 caracteres")
    private String identificador;
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "Máximo 120 caracteres")
    private String nombre;
    @Size(max = 25, message = "Máximo 25 caracteres")
    private String telefono;
    @Size(max = 150, message = "Máximo 150 caracteres")
    private String correo;
    @Size(max = 255, message = "Máximo 255 caracteres")
    private String direccion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getIdentificador() { return identificador; }
    public void setIdentificador(String identificador) { this.identificador = identificador; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
