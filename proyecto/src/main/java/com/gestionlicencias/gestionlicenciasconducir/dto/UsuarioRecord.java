package com.gestionlicencias.gestionlicenciasconducir.dto;

import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;

public record UsuarioRecord(
    String nombreUsuario,
    TipoDocumento tipoDocumento,
    String documento,
    String apellido,
    String nombre,
    String contrasena
) {}
