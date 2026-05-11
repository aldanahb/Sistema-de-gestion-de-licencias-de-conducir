package com.gestionlicencias.gestionlicenciasconducir.dto;

import java.util.Date;
import com.gestionlicencias.gestionlicenciasconducir.model.TipoDocumento;

public record LicenciaListadoRecord(
    String nombreCompletoTitular,
    TipoDocumento tipoDocumento,
    String documento,
    String clase,
    String estadoActual,
    Date fechaVencimiento
) {

}
