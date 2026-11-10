package com.ultimoarranque.android.model;

import java.math.BigDecimal;

public final class RepuestoBajo {
    private final String codigo;
    private final String nombre;
    private final int existencia;
    private final BigDecimal precio;

    public RepuestoBajo(String codigo, String nombre, int existencia, BigDecimal precio) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.existencia = existencia;
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getExistencia() {
        return existencia;
    }

    public BigDecimal getPrecio() {
        return precio;
    }
}
