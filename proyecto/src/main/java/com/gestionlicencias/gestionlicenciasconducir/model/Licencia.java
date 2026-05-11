package com.gestionlicencias.gestionlicenciasconducir.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "licencia")
public class Licencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_licencia")
    private Integer idLicencia;

    @Column(name = "clase", nullable = false)
    private String clase;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_inicio", nullable = false)
    private Date fechaInicio;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_vencimiento", nullable = false)
    private Date fechaVencimiento;

    @Column(name = "esta_vigente", nullable = false)
    private Boolean estaVigente;

    @Column(name = "observaciones", length = 100)
    private String observaciones;

    @ManyToOne
    @JoinColumn(name = "id_titular", nullable = false)
    private Titular titular;

    public Licencia() {
    }

    public Integer getIdLicencia() {
        return idLicencia;
    }

    public void setIdLicencia(Integer idLicencia) {
        this.idLicencia = idLicencia;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public Boolean getEstaVigente() {
        return estaVigente;
    }

    public void setEstaVigente(Boolean estaVigente) {
        this.estaVigente = estaVigente;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public Titular getTitular() {
        return titular;
    }

    public void setTitular(Titular titular) {
        this.titular = titular;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
