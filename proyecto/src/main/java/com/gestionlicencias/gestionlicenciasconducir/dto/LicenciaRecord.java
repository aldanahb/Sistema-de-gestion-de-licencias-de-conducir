package com.gestionlicencias.gestionlicenciasconducir.dto;

import jakarta.validation.constraints.NotNull;
import com.gestionlicencias.gestionlicenciasconducir.model.Licencia;
import java.sql.Date;

import org.hibernate.validator.constraints.Length; 

public record LicenciaRecord(
    @NotNull(message = "La clase de licencia es requerida")
    @Length(max = 1, message = "La clase de licencia debe tener como maximo 1 caracter")
    String clase,
    @Length(max = 100, message = "Las observaciones deben tener como maximo 100 caracteres")
    String observaciones,
    @NotNull(message = "El titular es requerido")
    TitularRecord titular,
    Date fechaVencimiento
) {
    public Licencia toLicencia() {
        Licencia licencia = new Licencia();
        licencia.setClase(this.clase);
        licencia.setObservaciones(this.observaciones);
        licencia.setFechaVencimiento(this.fechaVencimiento);
        licencia.setTitular(this.titular.toTitular());
        return licencia;
    }
}
