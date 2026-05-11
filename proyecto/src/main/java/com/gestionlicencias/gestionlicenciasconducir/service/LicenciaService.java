package com.gestionlicencias.gestionlicenciasconducir.service;

import java.util.List;
import java.util.Date;
import com.gestionlicencias.gestionlicenciasconducir.dto.LicenciaListadoRecord;
import com.gestionlicencias.gestionlicenciasconducir.dto.LicenciaRecord;
import com.gestionlicencias.gestionlicenciasconducir.model.Licencia;
import com.gestionlicencias.gestionlicenciasconducir.model.Titular;
import com.gestionlicencias.gestionlicenciasconducir.model.Tramite;

public interface LicenciaService {
    Float calcularCostoLicencia(String clase, Integer vigencia);
    Licencia obtenerUltimaLicenciaTitular(Titular titular);
    List<LicenciaRecord> buscarLicenciasVigentes(String nombreApellido, String grupoSanguineo, String factorRH, boolean donanteOrganos);
    List<LicenciaListadoRecord> buscarLicenciasNoVigentes(Date fechaDesde, Date fechaHasta, String clase);
    Licencia buscarLicenciaPorTitularyClase(Titular titular, String claseLicencia);
    Tramite emitirCopiaLicencia(Licencia licencia, Titular titular);
    Boolean sePuedeRenovar(Licencia licencia, Titular titular);
    Tramite renovarLicencia(Licencia licencia);
}
