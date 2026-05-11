package com.gestionlicencias.gestionlicenciasconducir.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "titular")
public class Titular{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_titular")
    private Integer idTitular;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 20, nullable = false)
    private TipoDocumento tipoDocumento;

    @Column(name = "documento", length = 8, nullable = false, unique = true)
    private String documento;

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "apellido", length = 50, nullable = false)
    private String apellido;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_nacimiento", nullable = false)
    private Date fechaNacimiento;

    @Column(name = "direccion", length = 100)
    private String direccion;

    @Column(name = "grupo_sanguineo", length = 3)
    private String grupoSanguineo;

    @Column(name = "factor_rh", length = 12)
    private String factorRH;

    @Column(name = "donante_organos", nullable = false)
    private Boolean donanteOrganos;

    @OneToMany(mappedBy = "titular", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Licencia> licencias = new ArrayList<>();

    @Column(name = "modificado")
    private Boolean modificado;

    public Titular() {
        modificado = false;
    }

    public Integer getIdTitular() {
        return idTitular;
    }

    public void setIdTitular(Integer idTitular) {
        this.idTitular = idTitular;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getGrupoSanguineo() {
        return grupoSanguineo;
    }

    public void setGrupoSanguineo(String grupoSanguineo) {
        this.grupoSanguineo = grupoSanguineo;
    }

    public String getFactorRH() {
        return factorRH;
    }

    public void setFactorRH(String factorRH) {
        this.factorRH = factorRH;
    }

    public Boolean getDonanteOrganos() {
        return donanteOrganos;
    }

    public void setDonanteOrganos(Boolean donanteOrganos) {
        this.donanteOrganos = donanteOrganos;
    }

    public Boolean getModificado() {
        return modificado;
    }

    public void setModificado(Boolean modificado) {
        this.modificado = modificado;
    }

    public List<Licencia> getLicencias() {
      return licencias;
    }

    public void addLicencia(Licencia licencia) {
        licencia.setTitular(this);
        this.licencias.add(licencia);
    }

    public LocalDate convertirDateALocalDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }

        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }
    public int getEdad() {
        LocalDate cumpleanios = convertirDateALocalDate(fechaNacimiento);
        LocalDate hoy = LocalDate.now();
        return Period.between(cumpleanios, hoy).getYears();
    }
}
