package com.gestionlicencias.gestionlicenciasconducir.dto;

import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.Past;

public record TitularRecord(
    @NotNull(message = "El tipo de documento no puede ser nulo")
    TipoDocumento tipoDocumento,
    @NotBlank(message = "El documento no puede estar vacío")
    @NotNull(message = "El documento no puede ser nulo")
    @Length(max = 8, message = "El documento debe tener como maximo 8 caracteres")
    String documento,
    /* @NotBlank(message = "El nombre no puede estar vacío")
    @NotNull(message = "El nombre no puede ser nulo") */
    @Length(max = 50, message = "El nombre debe tener como maximo 50 caracteres") 
    String nombre,
    /* @NotBlank(message = "El apellido no puede estar vacío")
    @NotNull(message = "El apellido no puede ser nulo") */
    @Length(max = 50, message = "El apellido debe tener como maximo 50 caracteres") 
    String apellido,
    /* @NotNull(message = "La fecha de nacimiento no puede ser nula") */
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada") 
    Date fechaNacimiento,
    /* @NotBlank(message = "La dirección no puede estar vacía")
    @NotNull(message = "La dirección no puede ser nula") */
    @Length(max = 100, message = "La dirección debe tener como maximo 100 caracteres")
    String direccion,
    /* @NotBlank(message = "El grupo sanguineo no puede estar vacío")
    @NotNull(message = "El grupo sanguineo no puede ser nulo") */
    @Length(max = 3, message = "El grupo sanguineo debe tener como maximo 3 caracteres")
    String grupoSanguineo,
    /* @NotBlank(message = "El factor RH no puede estar vacío")
    @NotNull(message = "El factor RH no puede ser nulo") */
    @Length(max = 12, message = "El factor RH debe tener como maximo 12 caracteres")
    String factorRH,
    /* @NotNull(message = "El donante de organos no puede ser nulo") */
    Boolean donanteOrganos
) {
    //Conviene sacar este metodo ya que se pasa la responsabilidad al mapper
    public Titular toTitular() {
        Titular titular = new Titular();
        titular.setTipoDocumento(this.tipoDocumento);
        titular.setDocumento(this.documento);
        titular.setNombre(this.nombre);
        titular.setApellido(this.apellido);
        titular.setFechaNacimiento(this.fechaNacimiento);
        titular.setDireccion(this.direccion);
        titular.setGrupoSanguineo(this.grupoSanguineo);
        titular.setFactorRH(this.factorRH);
        titular.setDonanteOrganos(this.donanteOrganos);
        
        return titular;
    }
    public String getDocumento() {
        return documento;
    }
    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }
}
