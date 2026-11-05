package com.ultimoarranque.model;

public class Vehiculo {
    private Long id;
    private Long clienteId;
    private String tipo, placa, marca, modelo;
    private Integer anio;
    public Vehiculo() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id=id; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long id) { this.clienteId=id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo=tipo; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa=placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca=marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo=modelo; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio=anio; }
}
