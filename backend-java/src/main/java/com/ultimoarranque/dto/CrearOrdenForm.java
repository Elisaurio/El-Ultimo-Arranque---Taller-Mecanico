package com.ultimoarranque.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CrearOrdenForm {
    @NotNull(message="Selecciona un vehículo")
    private Long vehiculoId;
    @NotNull(message="Selecciona un mecánico")
    private Long mecanicoId;
    @NotBlank(message="Describe el problema")
    @Size(max=5000, message="Descripción demasiado larga")
    private String problema;
    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId=vehiculoId; }
    public Long getMecanicoId() { return mecanicoId; }
    public void setMecanicoId(Long mecanicoId) { this.mecanicoId=mecanicoId; }
    public String getProblema() { return problema; }
    public void setProblema(String problema) { this.problema=problema; }
}
